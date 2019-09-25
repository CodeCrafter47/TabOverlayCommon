package de.codecrafter47.taboverlay.config.expression.token;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;

public class QuotedLiteralTokenReader extends TokenReader {
    private final char quote;

    public QuotedLiteralTokenReader(int priority, char quote) {
        super(priority);
        this.quote = quote;
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        if (position.getIndex() < text.length() && text.charAt(position.getIndex()) == quote) {
            int startIndex = position.getIndex();

            position.setIndex(position.getIndex() + 1);

            while (position.getIndex() < text.length() && quote != text.charAt(position.getIndex()))
                position.setIndex(position.getIndex() + 1);

            position.setIndex(position.getIndex() + 1);

            return new StringToken(text.substring(startIndex + 1, position.getIndex() - 1));
        }
        return null;
    }
}
