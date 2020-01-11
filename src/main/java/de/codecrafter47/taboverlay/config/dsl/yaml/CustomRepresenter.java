package de.codecrafter47.taboverlay.config.dsl.yaml;

import de.codecrafter47.taboverlay.util.Unchecked;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CustomRepresenter extends Representer {

    @Override
    public Node represent(Object data) {
        if (data instanceof MarkedFloatProperty) {
            return super.represent(((MarkedFloatProperty) data).getValue());
        } else if (data instanceof MarkedIntegerProperty) {
            return super.represent(((MarkedIntegerProperty) data).getValue());
        } else if (data instanceof MarkedListProperty) {
            return super.represent(new ArrayList<>(Unchecked.cast(data)));
        } else if (data instanceof MarkedMapProperty) {
            return super.represent(new LinkedHashMap<>(Unchecked.cast(data)));
        } else if (data instanceof MarkedStringProperty) {
            return super.represent(((MarkedStringProperty) data).getValue());
        }
        return super.represent(data);
    }

    @Override
    protected Node representScalar(Tag tag, String value) {
        return super.representScalar(tag, value, value.contains("\n") ? DumperOptions.ScalarStyle.LITERAL : DumperOptions.ScalarStyle.PLAIN);
    }
}
