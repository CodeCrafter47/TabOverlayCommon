package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

public interface PlaceholderResolver {

    /**
     * Resolves a placeholder
     *
     * @param value placeholder (without ${})
     * @param tcc
     * @return the placeholder
     * @throws UnknownPlaceholderException if the placeholder cannot be resolved
     */
    Placeholder resolve(String[] value, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException;
}
