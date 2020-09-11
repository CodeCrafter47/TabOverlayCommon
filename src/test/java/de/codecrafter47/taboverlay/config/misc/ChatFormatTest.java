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

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatFormatTest {

    @Test
    public void textHexColorCompatibility() {
        String json = ChatFormat.formattedTextToJson("§x§r§r§g§g§b§bTest");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#rrggbb\"}]}", json);
    }

    @Test
    public void textHexColorOption2() {
        String json = ChatFormat.formattedTextToJson("&#012345Test");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#012345\"}]}", json);
    }

    @Test
    public void textHexColorCMI() {
        String json = ChatFormat.formattedTextToJson("{#012345}Test");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#012345\"}]}", json);
    }
}