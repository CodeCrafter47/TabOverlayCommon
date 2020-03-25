package de.codecrafter47.taboverlay.config.dsl.yaml;

import de.codecrafter47.taboverlay.config.dsl.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.util.Unchecked;
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
        representers.put(CustomPlaceholderConfiguration.Alias.class, data -> representData(((CustomPlaceholderConfiguration.Alias) data).getReplacement()));
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
