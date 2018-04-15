/*
 * BungeeTabListPlus - a BungeeCord plugin to customize the tablist
 *
 * Copyright (C) 2014 - 2015 Florian Stober
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay.config.expression.token;

import com.google.common.collect.Ordering;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

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

    public List<Token> parse(TemplateCreationContext tcc, String input, Mark mark) {
        State state = new State(input);

        List<Token> tokens = new LinkedList<>();

        next_token:
        while (true) {
            // skip spaces
            while (state.index < state.input.length() && Character.isWhitespace(state.input.charAt(state.index))) {
                state.index += 1;
            }

            if (state.index >= state.input.length()) {
                break;
            }

            for (TokenReader tokenReader : tokenReaders) {
                Token token;
                if (null != (token = tokenReader.read(state))) {
                    tokens.add(token);
                    continue next_token;
                }
            }

            tcc.getErrorHandler().addError(format("Illegal token '%c' at index %d in \"%s\"", state.input.charAt(state.index), state.index, state.input), mark);
            break;
        }

        return tokens;
    }

    public class State {
        public String input;
        public int index;

        public State(String input) {
            this.input = input;
            this.index = 0;
        }
    }
}
