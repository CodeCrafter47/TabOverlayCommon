package de.codecrafter47.taboverlay.config.expression.token;

import de.codecrafter47.taboverlay.config.placeholder.PlaceholderParser;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;

public class PlaceholderTokenReader extends TokenReader {
    public PlaceholderTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        if (position.getIndex() + 1 < text.length() && text.charAt(position.getIndex()) == '$' && text.charAt(position.getIndex() + 1) == '{') {
            position.setIndex(position.getIndex() + 2);
            return new PlaceholderToken(PlaceholderParser.parse(text, position, mark, tcc));
        }
        return null;
    }
}
