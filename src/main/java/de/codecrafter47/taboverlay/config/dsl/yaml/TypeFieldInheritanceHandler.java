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
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.Map;

@RequiredArgsConstructor
public class TypeFieldInheritanceHandler extends AbstractInheritanceHandler {
    private final String field;
    private final Map<String, Class<?>> subtypes;
    private final Class<?> defaultType;

    @Override
    protected Class<?> getType(Node node) {
        if (node instanceof MappingNode) {
            Node node1 = YamlUtil.get((MappingNode) node, field);
            if (node1 != null && node1 instanceof ScalarNode) {
                String value = ((ScalarNode) node1).getValue();
                Class<?> type = subtypes.get(value);
                if (type != null) {
                    YamlUtil.remove((MappingNode) node, field);
                    return type;
                } else {
                    ErrorHandler.get().addError("Unknown value set for " + field + ": " + value, node1.getStartMark());
                    ErrorHandler.get().stopAccepting();
                }
            }
        }
        if (defaultType == null) {
            ErrorHandler.get().addError("The " + field + " option must be specified to construct " + node.getType().getSimpleName(), node.getStartMark());
            ErrorHandler.get().stopAccepting();
        }
        return defaultType;
    }
}
