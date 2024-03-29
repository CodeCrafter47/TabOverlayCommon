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

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.expression.token.Token;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

@RequiredArgsConstructor
public class NegatedNumberReader extends ValueReader {

    private final Token TOKEN_NEGATION;

    @Override
    public ExpressionTemplate read(TemplateCreationContext tcc, ExpressionTemplateParser parser, List<Token> tokenList, Mark mark) {
        if (tokenList.get(0) == TOKEN_NEGATION) {
            tokenList.remove(0);
            return ExpressionTemplates.negateNumber(parser.read(tcc, tokenList, mark));
        }
        return null;
    }
}
