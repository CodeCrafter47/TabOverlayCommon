package de.codecrafter47.taboverlay.config.dsl.yaml;

import de.codecrafter47.taboverlay.config.ErrorHandler;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class CustomPropertyUtils extends PropertyUtils {

    @Override
    public Property getProperty(Class<?> type, String name, BeanAccess bAccess) {
        Property property = super.getProperty(type, name.replace('-', '_'), bAccess);
        if (property instanceof MissingProperty) {
            return new MissingProperty(name) {
                @Override
                public void set(Object object, Object value) throws Exception {
                    ErrorHandler.get().addWarning("Unknown config option: " + getName(), null);
                }
            };
        }
        return property;
    }
}
