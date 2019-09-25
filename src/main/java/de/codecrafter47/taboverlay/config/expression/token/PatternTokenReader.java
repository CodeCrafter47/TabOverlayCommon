package de.codecrafter47.taboverlay.config.expression.token;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;

public class PatternTokenReader extends TokenReader {
    private final Token token;
    private final String pattern;
    private final boolean ignoreCase;

    public PatternTokenReader(Token token, String pattern) {
        this(token, pattern, false);
    }

    public PatternTokenReader(Token token, String pattern, boolean ignoreCase) {
        super(pattern.length());
        this.token = token;
        this.pattern = pattern;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        if (text.regionMatches(ignoreCase, position.getIndex(), pattern, 0, pattern.length())) {
            position.setIndex(position.getIndex() + pattern.length());
            return token;
        }
        return null;
    }
}
