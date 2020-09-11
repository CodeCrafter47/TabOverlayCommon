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

package de.codecrafter47.taboverlay.config.expression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.codecrafter47.taboverlay.config.expression.operators.ListOperator;
import de.codecrafter47.taboverlay.config.expression.operators.Operator;
import de.codecrafter47.taboverlay.config.expression.parser.*;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.expression.token.*;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Builder;
import lombok.Singular;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

public class DefaultExpressionEngine implements ExpressionEngine {

    private final ExpressionTokenizer tokenizer;
    private final ExpressionTemplateParser templateParser;

    public DefaultExpressionEngine(Options options) {
        this.tokenizer = new ExpressionTokenizer(options.tokenReaders);
        this.templateParser = new ExpressionTemplateParser(options.operators, options.valueReaders);
    }

    @Override
    public ExpressionTemplate compile(TemplateCreationContext tcc, String expression, Mark mark) {
        return templateParser.parse(tcc, tokenizer.parse(tcc, expression, mark), mark);
    }

    @Builder
    public static class Options {
        @Singular
        private List<TokenReader> tokenReaders;

        @Singular
        private ImmutableMap<Token, Operator> operators;

        @Singular
        private ImmutableList<ValueReader> valueReaders;

        public static class OptionsBuilder {

            public OptionsBuilder withDefaultTokenReaders() {
                return this
                        .tokenReader(new PatternTokenReader(BooleanToken.FALSE, "false"))
                        .tokenReader(new PatternTokenReader(BooleanToken.TRUE, "true"))
                        .tokenReader(new PatternTokenReader(BooleanToken.TRUE, "all"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.AND, "and"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.AND, "&&"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.OR, "or"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.OR, "||"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.EQUAL, "=="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.EQUAL, "="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.NOT_EQUAL, "!="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.GREATER_OR_EQUAL_THAN, ">="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.LESSER_OR_EQUAL_THAN, "<="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.OPENING_PARENTHESIS, "("))
                        .tokenReader(new PatternTokenReader(DefaultTokens.CLOSING_PARENTHESIS, ")"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.NEGATION, "!"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.GREATER_THAN, ">"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.LESSER_THAN, "<"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.CONCAT_STRING, "."))
                        .tokenReader(new PatternTokenReader(DefaultTokens.ADD, "+"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.SUB, "-"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.MULT, "*"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.DIV, "/"))
                        .tokenReader(new QuotedLiteralTokenReader(-10, '\"'))
                        .tokenReader(new QuotedLiteralTokenReader(-10, '\''))
                        .tokenReader(new PlaceholderTokenReader(-20))
                        .tokenReader(new NumberTokenReader(-50))
                        .tokenReader(new NonQuotedLiteralTokenReader(-100));
            }

            public OptionsBuilder withDefaultOperators() {
                return this
                        .operator(DefaultTokens.AND, ListOperator.of(100, ExpressionTemplates::and))
                        .operator(DefaultTokens.OR, ListOperator.of(50, ExpressionTemplates::or))
                        .operator(DefaultTokens.EQUAL, Operator.of(25, ExpressionTemplates::equal))
                        .operator(DefaultTokens.NOT_EQUAL, Operator.of(25, ExpressionTemplates::notEqual))
                        .operator(DefaultTokens.GREATER_THAN, Operator.of(25, ExpressionTemplates::greater))
                        .operator(DefaultTokens.GREATER_OR_EQUAL_THAN, Operator.of(25, ExpressionTemplates::greaterOrEqual))
                        .operator(DefaultTokens.LESSER_THAN, Operator.of(25, ExpressionTemplates::less))
                        .operator(DefaultTokens.LESSER_OR_EQUAL_THAN, Operator.of(25, ExpressionTemplates::lessOrEqual))
                        .operator(DefaultTokens.CONCAT_STRING, ListOperator.of(10, ExpressionTemplates::concat))
                        .operator(DefaultTokens.ADD, ListOperator.of(4, ExpressionTemplates::sum))
                        .operator(DefaultTokens.SUB, Operator.of(3, ExpressionTemplates::sub))
                        .operator(DefaultTokens.MULT, ListOperator.of(2, ExpressionTemplates::product))
                        .operator(DefaultTokens.DIV, Operator.of(1, ExpressionTemplates::div));
            }

            public OptionsBuilder withDefaultValueReaders() {
                return this
                        .valueReader(new BooleanConstantReader())
                        .valueReader(new NumberConstantReader())
                        .valueReader(new StringConstantReader())
                        .valueReader(new NegatedExpressionReader(DefaultTokens.NEGATION))
                        .valueReader(new ParenthesisedExpressionReader(DefaultTokens.OPENING_PARENTHESIS, DefaultTokens.CLOSING_PARENTHESIS))
                        .valueReader(new PlaceholderReader())
                        .valueReader(new NegatedNumberReader(DefaultTokens.SUB));
            }
        }
    }
}
