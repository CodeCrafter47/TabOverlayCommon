package de.codecrafter47.taboverlay.config.expression.token;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;

public abstract class TokenReader {
    @Getter
    private final int priority;

    public TokenReader(int priority) {
        this.priority = priority;
    }

    public abstract Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc);

}
