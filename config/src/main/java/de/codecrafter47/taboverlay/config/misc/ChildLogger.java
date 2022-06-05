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

package de.codecrafter47.taboverlay.config.misc;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ChildLogger extends Logger {

    private final String prefix;

    public ChildLogger(Logger parent, String name) {
        super(parent.getName() + "." + name, null);
        this.prefix = "[" + name + "] ";
        setParent(parent);
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(prefix + record.getMessage());
        super.log(record);
    }
}
