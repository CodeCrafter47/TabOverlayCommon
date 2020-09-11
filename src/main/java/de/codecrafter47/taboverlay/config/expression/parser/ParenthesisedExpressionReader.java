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
import de.codecrafter47.taboverlay.config.expression.token.Token;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ParenthesisedExpressionReader extends ValueReader {

    private final Token OPENING_PARENTHESIS;
    private final Token CLOSING_PARENTHESIS;

    @Override
    public ExpressionTemplate read(TemplateCreationContext tcc, ExpressionTemplateParser parser, List<Token> tokenList, Mark mark) {
        if (tokenList.get(0) == OPENING_PARENTHESIS) {
            int index = 0;
            int cnt = 1;
            do {
                index += 1;
                if (tokenList.size() <= index) {
                    return null;
                }

                Token token = tokenList.get(index);
                if (token == OPENING_PARENTHESIS) {
                    cnt++;
                } else if (token == CLOSING_PARENTHESIS) {
                    cnt--;
                }
            } while (cnt != 0);

            ExpressionTemplate result = parser.parse(tcc, new ArrayList<>(tokenList.subList(1, index)), mark);
            for (int i = 0; i <= index; i++) {
                tokenList.remove(0);
            }
            return result;
        }
        return null;
    }
}
