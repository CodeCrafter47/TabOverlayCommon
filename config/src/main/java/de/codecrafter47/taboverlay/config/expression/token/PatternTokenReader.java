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

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;

public class PatternTokenReader extends TokenReader {
    private final Token token;
    private final String pattern;
    private final boolean ignoreCase;

    public PatternTokenReader(Token token, String pattern) {
        this(token, pattern, true);
    }

    public PatternTokenReader(Token token, String pattern, boolean ignoreCase) {
        super(pattern.length());
        this.token = token;
        this.pattern = pattern;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        if (text.regionMatches(ignoreCase, position.getIndex(), pattern, 0, pattern.length())) {
            int newIndex = position.getIndex() + pattern.length();
            if (((newIndex + 1) < text.length() && Character.isWhitespace(text.charAt(newIndex + 1))) || (newIndex == (text.length() - 1))) {
                position.setIndex(position.getIndex() + pattern.length());
                return token;
            }
        }
        return null;
    }
}
