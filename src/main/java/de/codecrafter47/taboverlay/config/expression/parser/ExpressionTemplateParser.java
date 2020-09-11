/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay.config.expression.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.codecrafter47.taboverlay.config.expression.operators.ListOperator;
import de.codecrafter47.taboverlay.config.expression.operators.Operator;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.expression.token.Token;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ExpressionTemplateParser {

    private final ImmutableMap<Token, Operator> operators;
    private final ImmutableList<ValueReader> valueReaders;

    public ExpressionTemplate parse(TemplateCreationContext tcc, List<Token> tokens, Mark mark) {
        List<ExpressionTemplate> parts = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();

        try {
            parts.add(read(tcc, tokens, mark));
        } catch (IllegalArgumentException e) {
            tcc.getErrorHandler().addError(e.getMessage(), mark);
            return ConstantExpressionTemplate.of(""); // dummy
        }

        while (!tokens.isEmpty()) {
            Token token = tokens.remove(0);
            val operator = this.operators.get(token);
            if (operator == null) {
                tcc.getErrorHandler().addError("Error parsing expression: Got " + token + " expected OPERATOR", mark);
                return ConstantExpressionTemplate.of(""); // dummy
            }
            operators.add(operator);
            if (tokens.isEmpty()) {
                tcc.getErrorHandler().addError("Unexpected end of input", mark);
                return ConstantExpressionTemplate.of(""); // dummy
            }

            try {
                parts.add(read(tcc, tokens, mark));
            } catch (IllegalArgumentException e) {
                tcc.getErrorHandler().addError(e.getMessage(), mark);
                return ConstantExpressionTemplate.of(""); // dummy
            }
        }

        while (!operators.isEmpty()) {
            Operator operator = operators.get(0);
            int lowest = operator.getPriority();
            int start = 0;
            int end = 1;
            for (int i = 1; i < operators.size(); i++) {
                operator = operators.get(i);
                if (operator.getPriority() < lowest) {
                    lowest = operator.getPriority();
                    start = i;
                    end = i + 1;
                } else if (operator.getPriority() > lowest) {
                    break;
                } else {
                    end++;
                }
            }

            operator = operators.get(start);

            ExpressionTemplate replacement;
            if (start + 1 == end) {
                replacement = operator.createTemplate(parts.get(start), parts.get(end));
            } else if (operator instanceof ListOperator) {
                replacement = ((ListOperator) operator).createTemplate(new ArrayList<>(parts.subList(start, end + 1)));
            } else {
                List<ExpressionTemplate> conditions = new ArrayList<>(end - start);
                for (int i = start; i < end; i++) {
                    conditions.add(operators.get(i).createTemplate(parts.get(i), parts.get(i + 1)));
                }
                replacement = ExpressionTemplates.and(conditions);
            }
            for (int i = start; i < end; i++) {
                parts.remove(start);
                operators.remove(start);
            }
            parts.set(start, replacement);
        }
        return parts.get(0);
    }

    ExpressionTemplate read(TemplateCreationContext tcc, List<Token> tokens, Mark mark) {
        for (val valueReader : valueReaders) {
            ExpressionTemplate template = valueReader.read(tcc, this, tokens, mark);
            if (template != null) {
                return template;
            }
        }
        throw new IllegalArgumentException("Invalid Expression Syntax. Got Token " + tokens.get(0).toString() + " expected literal or placeholder.");
    }
}
