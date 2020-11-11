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

import de.codecrafter47.taboverlay.config.dsl.customplaceholder.CustomPlaceholderAliasConfiguration;
import de.codecrafter47.taboverlay.config.dsl.customplaceholder.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.config.misc.Unchecked;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CustomRepresenter extends Representer {

    public CustomRepresenter() {
        representers.put(MarkedFloatProperty.class, data -> representData(((MarkedFloatProperty) data).getValue()));
        representers.put(MarkedBooleanProperty.class, data -> representData(((MarkedBooleanProperty) data).isValue()));
        representers.put(MarkedIntegerProperty.class, data -> representData(((MarkedIntegerProperty) data).getValue()));
        representers.put(MarkedStringProperty.class, data -> representData(((MarkedStringProperty) data).getValue()));
        representers.put(MarkedListProperty.class, data -> representData(new ArrayList<>(Unchecked.cast(data))));
        representers.put(MarkedMapProperty.class, data -> representData(new LinkedHashMap<>(Unchecked.cast(data))));
        representers.put(CustomPlaceholderAliasConfiguration.class, data -> representData(((CustomPlaceholderAliasConfiguration) data).getReplacement()));
    }

    @Override
    protected Node representScalar(Tag tag, String value) {
        if (value.contains("\n")) {
            return super.representScalar(tag, value, DumperOptions.ScalarStyle.LITERAL);
        }
        return super.representScalar(tag, value, DumperOptions.ScalarStyle.PLAIN);
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (javaBean instanceof CustomPlaceholderConfiguration && "parameters".equals(property.getName()) && ((MarkedIntegerProperty) propertyValue).getValue() == 0) {
            return null;
        } else {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            if ("true".equals(property.getName()) || "false".equals(property.getName())) {
                tuple.getKeyNode().setTag(Tag.BOOL);
            }
            return tuple;
        }
    }
}
