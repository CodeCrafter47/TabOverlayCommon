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

package de.codecrafter47.taboverlay.config.expression.token;

import com.google.common.collect.Ordering;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public class ExpressionTokenizer {

    private static final Ordering<TokenReader> TOKEN_READER_ORDERING
            = Ordering.from(Comparator.comparingInt(TokenReader::getPriority)).reverse();

    private final List<TokenReader> tokenReaders;

    public ExpressionTokenizer(Iterable<TokenReader> tokenReaders) {
        this.tokenReaders = TOKEN_READER_ORDERING.immutableSortedCopy(tokenReaders);
    }

    public List<Token> parse(TemplateCreationContext tcc, String text, Mark mark) {
        ParsePosition position = new ParsePosition(0);

        List<Token> tokens = new LinkedList<>();

        next_token:
        while (true) {
            // skip spaces
            while (position.getIndex() < text.length() && Character.isWhitespace(text.charAt(position.getIndex()))) {
                position.setIndex(position.getIndex() + 1);
            }

            if (position.getIndex() >= text.length()) {
                break;
            }

            for (TokenReader tokenReader : tokenReaders) {
                Token token;
                if (null != (token = tokenReader.read(text, position, mark, tcc))) {
                    tokens.add(token);
                    continue next_token;
                }
            }

            tcc.getErrorHandler().addError(format("Illegal token '%c' at index %d in \"%s\"", text.charAt(position.getIndex()), position.getIndex(), text), mark);
            break;
        }

        return tokens;
    }
}
