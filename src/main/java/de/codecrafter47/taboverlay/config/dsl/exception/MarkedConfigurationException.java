package de.codecrafter47.taboverlay.config.dsl.exception;

import lombok.Getter;
import org.yaml.snakeyaml.error.Mark;

public class MarkedConfigurationException extends ConfigurationException {

    private static final long serialVersionUID = -1583720337376504279L;

    @Getter
    private final Mark mark;

    public MarkedConfigurationException(String message) {
        this(message, null, null);
    }

    public MarkedConfigurationException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public MarkedConfigurationException(String message, Mark mark) {
        this(message, mark, null);
    }

    public MarkedConfigurationException(String message, Mark mark, Throwable cause) {
        super(message, cause);
        this.mark = mark;
    }
}
