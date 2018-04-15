package de.codecrafter47.taboverlay.config;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import lombok.Value;

@Value
public class ComponentSpec {
    /**
     * Tag (with !).
     */
    private String tag;

    /**
     * Class to use for config deserialization.
     */
    private Class<? extends ComponentConfiguration> configurationClass;
}
