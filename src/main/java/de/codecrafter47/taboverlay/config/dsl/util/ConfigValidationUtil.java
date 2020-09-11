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

package de.codecrafter47.taboverlay.config.dsl.util;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.util.Collection;

public class ConfigValidationUtil {

    public static boolean isRectangular(int size) {
        int columns = (size + 19) / 20;
        int rows = columns != 0 ? size / columns : 1;
        return columns * rows == size;
    }

    public static boolean checkNotNull(TemplateCreationContext tcc, String context, String attributeName, Object value, Mark mark) {
        if (value == null) {
            tcc.getErrorHandler().addError("Failed to configure " + context + ", " + attributeName + " is not set/ set to null.", mark);
            return false;
        }
        return true;
    }

    public static boolean checkNotEmpty(TemplateCreationContext tcc, String context, String attributeName, Collection<?> value, Mark mark) {
        if (value.isEmpty()) {
            tcc.getErrorHandler().addError("Failed to configure " + context + ", " + attributeName + " is empty.", mark);
            return false;
        }
        return true;
    }

    public static boolean checkRange(TemplateCreationContext tcc, String context, String attributeName, int actual, int min, int max, Mark mark) {
        if (actual > max || actual < min) {
            tcc.getErrorHandler().addError("Failed to configure " + context + ", " + attributeName + "(" + actual + ") out of range. Must be between " + min + " and " + " max.", mark);
            return false;
        }
        return true;
    }

    public static boolean checkRange(TemplateCreationContext tcc, String context, String attributeName, float actual, float min, float max, Mark mark) {
        if (actual > max || actual < min) {
            tcc.getErrorHandler().addError("Failed to configure " + context + ", " + attributeName + "(" + actual + ") out of range. Must be between " + min + " and " + " max.", mark);
            return false;
        }
        return true;
    }

    public static boolean checkRange(TemplateCreationContext tcc, String context, String attributeName, double actual, double min, double max, Mark mark) {
        if (actual > max || actual < min) {
            tcc.getErrorHandler().addError("Failed to configure " + context + ", " + attributeName + "(" + actual + ") out of range. Must be between " + min + " and " + " max.", mark);
            return false;
        }
        return true;
    }
}
