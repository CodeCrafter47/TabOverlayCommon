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
import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.BasicComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.ListComponentConfiguration;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Map;

public class ComponentConfigurationInheritanceHandler extends TagInheritanceHandler {
    public ComponentConfigurationInheritanceHandler(Map<String, Class<?>> subtypes) {
        super(subtypes, BasicComponentConfiguration.class);
    }

    @Override
    public void handle(Node node) {
        if (node instanceof SequenceNode) {
            node.setTag(Tag.SEQ);
            ((SequenceNode) node).setListType(ComponentConfiguration.class);
            node.setType(ListComponentConfiguration.class);
        } else {
            super.handle(node);
        }
    }

    @Override
    protected Class<?> unknownTag(Node node) {
        ErrorHandler.get().addError("Unknown component " + node.getTag().getValue(), node.getStartMark());
        ErrorHandler.get().stopAccepting();
        return super.unknownTag(node);
    }
}
