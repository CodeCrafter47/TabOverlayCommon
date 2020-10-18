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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class NumberTokenReader extends TokenReader {

    private NumberFormat format = NumberFormat.getInstance(Locale.ROOT);

    public NumberTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        int previous = position.getIndex();
        Number number = format.parse(text, position);
        if (position.getIndex() != previous) {
            return new NumberToken(number.doubleValue());
        }
        return null;
    }
}
