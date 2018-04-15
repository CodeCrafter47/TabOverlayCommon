package de.codecrafter47.taboverlay.config.dsl.yaml;

import com.google.common.collect.ImmutableMap;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomYamlConstructor extends Constructor {

    private final ImmutableMap<Class<?>, InheritanceHandler> typeInheritanceHandlerMap;

    private final Map<Tag, Class<?>> tagToClassMap = new HashMap<>();
    private final static Pattern PATTERN_UNKNOWN_PROPERTY = Pattern.compile("Unable to find property '(.*)' on class");

    public CustomYamlConstructor(ImmutableMap<Class<?>, InheritanceHandler> typeInheritanceHandlerMap) {
        this.typeInheritanceHandlerMap = typeInheritanceHandlerMap;
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping() {
            @Override
            protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
                try {
                    // todo better way to do this
                    return super.constructJavaBean2ndStep(node, object);
                } catch (ConstructorException e) {
                    Matcher matcher = PATTERN_UNKNOWN_PROPERTY.matcher(e.getMessage());
                    if (matcher.find()) {
                        String attribute = matcher.group(1);
                        ErrorHandler.get().addError("Unknown option " + attribute + " in " + node.getType().getSimpleName(), e.getProblemMark());
                    } else {
                        throw e;
                    }
                    return object;
                }
            }
        });
    }

    @Override
    protected void flattenMapping(MappingNode node) {
        // do nothing
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
        try {
            return Class.forName(tag.getClassName());
        } catch (ClassNotFoundException | NullPointerException | YAMLException ignore) {
        }
        return null;
    }
}
