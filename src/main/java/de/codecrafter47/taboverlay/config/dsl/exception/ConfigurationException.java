package de.codecrafter47.taboverlay.config.dsl.exception;

/**
 * An exception that occurs while creating a template from a configuration.
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = -8227615185391800203L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
