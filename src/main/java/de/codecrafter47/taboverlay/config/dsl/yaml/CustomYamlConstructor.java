package de.codecrafter47.taboverlay.config.dsl.yaml;

import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.*;
import java.util.regex.Pattern;

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
    private final static Pattern PATTERN_UNKNOWN_PROPERTY = Pattern.compile("Unable to find property '(.*)' on class");

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
        // todo do we need that? ensureTypeDefinitionPresent(node.getType());
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

    private InheritanceHandler getInheritanceHandler(Node node) {
        InheritanceHandler inheritanceHandler;
        if (null != (inheritanceHandler = typeInheritanceHandlerMap.get(tagToClassMap.computeIfAbsent(node.getTag(), this::getClassForTag)))) {
            return inheritanceHandler;
        }
        return typeInheritanceHandlerMap.get(node.getType());
    }

    private void ensureTypeDefinitionPresent(Class<?> type) {
        if (!typeDefinitions.containsKey(type)) {
            addTypeDescription(computeTypeDescription(type));
        }
    }

    private TypeDescription computeTypeDescription(Class<?> clazz) {
        TypeDescription typeDescription = new TypeDescription(clazz, new Tag(clazz));
        Set<Property> properties = null;
        properties = getPropertyUtils().getProperties(clazz);
        if (properties != null) {
            for (Property property : properties) {
                if (Collection.class.isAssignableFrom(property.getType()) || property.getClass().isArray()) {
                    Class<?>[] typeArguments = property.getActualTypeArguments();
                    if (typeArguments != null && typeArguments.length == 1 && typeArguments[0] != null) {
                        typeDescription.addPropertyParameters(property.getName(), typeArguments[0]);
                    }
                }
                if (Map.class.isAssignableFrom(property.getType())) {
                    Class<?>[] typeArguments = property.getActualTypeArguments();
                    if (typeArguments != null && typeArguments.length == 2 && typeArguments[0] != null && typeArguments[1] != null) {
                        typeDescription.addPropertyParameters(property.getName(), typeArguments[0], typeArguments[1]);
                    }
                }
            }
        }
        return typeDescription;
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
