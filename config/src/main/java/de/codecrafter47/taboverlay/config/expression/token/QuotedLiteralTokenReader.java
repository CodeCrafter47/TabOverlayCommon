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

public class QuotedLiteralTokenReader extends TokenReader {
    private final char quote;

    public QuotedLiteralTokenReader(int priority, char quote) {
        super(priority);
        this.quote = quote;
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        if (position.getIndex() < text.length() && text.charAt(position.getIndex()) == quote) {
            int startIndex = position.getIndex();

            position.setIndex(position.getIndex() + 1);

            while (position.getIndex() < text.length() && quote != text.charAt(position.getIndex()))
                position.setIndex(position.getIndex() + 1);

            position.setIndex(position.getIndex() + 1);

            return new StringToken(text.substring(startIndex + 1, position.getIndex() - 1));
        }
        return null;
    }
}
