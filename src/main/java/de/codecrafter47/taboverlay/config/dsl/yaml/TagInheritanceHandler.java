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
import org.yaml.snakeyaml.nodes.Node;

import java.util.Map;

@RequiredArgsConstructor
public class TagInheritanceHandler extends AbstractInheritanceHandler {

    private final Map<String, Class<?>> subtypes;
    private final Class<?> defaultType;

    @Override
    protected Class<?> getType(Node node) {
        Class<?> type = subtypes.get(node.getTag().getValue());
        if (type != null) {
            return type;
        }
        if (node.getTag().isSecondary()) {
            return unknownTag(node);
        }
        return defaultType;
    }

    protected Class<?> unknownTag(Node node) {
        ErrorHandler.get().addError("Unknown tag " + node.getTag().getValue(), node.getStartMark());
        ErrorHandler.get().stopAccepting();
        return defaultType;
    }
}
