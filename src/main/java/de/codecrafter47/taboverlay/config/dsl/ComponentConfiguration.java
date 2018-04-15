package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.exception.MarkedConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedProperty;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;

/**
 * Marker interface for a component configuration
 */
public interface ComponentConfiguration extends MarkedProperty {
    ComponentTemplate toTemplate(TemplateCreationContext tcc) throws ConfigurationException;
}
