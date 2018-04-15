package de.codecrafter47.taboverlay.config.dsl.yaml;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class AbstractInheritanceHandler implements InheritanceHandler {

    @Override
    public void handle(Node node) {
        Class<?> type = getType(node);
        if (type != null) {
            node.setType(type);
            node.setUseClassConstructor(true);
        }
    }

    protected abstract Class<?> getType(Node node);
}
