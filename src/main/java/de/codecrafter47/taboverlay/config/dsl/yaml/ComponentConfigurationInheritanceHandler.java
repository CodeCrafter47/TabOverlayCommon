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
