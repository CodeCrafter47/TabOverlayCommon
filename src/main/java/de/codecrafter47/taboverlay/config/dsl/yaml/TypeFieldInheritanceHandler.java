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
