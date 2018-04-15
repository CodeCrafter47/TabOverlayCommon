package de.codecrafter47.taboverlay.config;

import de.codecrafter47.taboverlay.config.dsl.AbstractTabOverlayTemplateConfiguration;
import lombok.Value;

@Value
public class TabOverlayTypeSpec {
    /**
     * Id to use for the type option of the config.
     */
    private String id;

    /**
     * Class to use for config deserialization.
     */
    private Class<? extends AbstractTabOverlayTemplateConfiguration> configurationClass;
}
