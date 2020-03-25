package de.codecrafter47.taboverlay.config.dsl.yaml;

import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomYamlConstructor extends Constructor {
    private static final Set<Tag> PRIMITIVE_TAGS = new HashSet<Tag>() {{
        add(Tag.YAML);
        add(Tag.MERGE);
        add(Tag.SET);
        add(Tag.PAIRS);
        add(Tag.OMAP);
        add(Tag.BINARY);
        add(Tag.INT);
        add(Tag.FLOAT);
        add(Tag.TIMESTAMP);
        add(Tag.BOOL);
        add(Tag.NULL);
        add(Tag.STR);
        add(Tag.SEQ);
        add(Tag.MAP);
    }};

    private final ImmutableMap<Class<?>, InheritanceHandler> typeInheritanceHandlerMap;

    private final Map<Tag, Class<?>> tagToClassMap = new HashMap<>();

    public CustomYamlConstructor(ImmutableMap<Class<?>, InheritanceHandler> typeInheritanceHandlerMap) {
        this.typeInheritanceHandlerMap = typeInheritanceHandlerMap;
    }

    @Override
    protected void processDuplicateKeys(MappingNode node) {
        for (NodeTuple tuple : node.getValue()) {
            Node key = tuple.getKeyNode();
            Tag tag = key.getTag();
            if (Tag.INT.equals(tag) || Tag.BOOL.equals(tag)) {
                key.setTag(Tag.STR);
            }
        }

        super.processDuplicateKeys(node);
    }

    @Override
    protected Object constructObject(Node node) {
        Object object = super.constructObject(node);
        if (object instanceof MarkedPropertyBase) {
            ((MarkedProperty) object).setStartMark(node.getStartMark());
        }
        return object;
    }

    @Override
    protected Construct getConstructor(Node node) {
        InheritanceHandler inheritanceHandler = null;
        InheritanceHandler newInheritanceHandler;
        while (inheritanceHandler != (newInheritanceHandler = getInheritanceHandler(node))
                && newInheritanceHandler != null) {
            inheritanceHandler = newInheritanceHandler;
            inheritanceHandler.handle(node);
        }
        return super.getConstructor(node);
    }

    @Override
    protected Object newInstance(Node node) {
        Object instance = super.newInstance(node);
        if (node instanceof MappingNode && instance instanceof UpdateableConfig) {
            ((UpdateableConfig) instance).update((MappingNode) node);
        }
        return instance;
    }

    private InheritanceHandler getInheritanceHandler(Node node) {
        InheritanceHandler inheritanceHandler;
        if (null != (inheritanceHandler = typeInheritanceHandlerMap.get(tagToClassMap.computeIfAbsent(node.getTag(), this::getClassForTag)))) {
            return inheritanceHandler;
        }
        return typeInheritanceHandlerMap.get(node.getType());
    }

    private Class<?> getClassForTag(Tag tag) {
        if (!PRIMITIVE_TAGS.contains(tag)) {
            try {
                return Class.forName(tag.getClassName());
            } catch (ClassNotFoundException | NullPointerException | YAMLException ignore) {
            }
        }
        return null;
    }

    @Override
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name, true, CustomYamlConstructor.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return Class.forName(name);
        }
    }
}
