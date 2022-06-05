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

package de.codecrafter47.taboverlay.config.dsl.yaml;

import de.codecrafter47.taboverlay.config.ErrorHandler;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class CustomPropertyUtils extends PropertyUtils {

    @Override
    public Property getProperty(Class<?> type, String name, BeanAccess bAccess) {
        Property property = super.getProperty(type, name.replace('-', '_'), bAccess);
        if (property instanceof MissingProperty) {
            return new MissingProperty(name) {
                @Override
                public void set(Object object, Object value) throws Exception {
                    ErrorHandler.get().addWarning("Unknown config option: " + getName(), null);
                }
            };
        }
        return property;
    }
}
