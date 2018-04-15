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
