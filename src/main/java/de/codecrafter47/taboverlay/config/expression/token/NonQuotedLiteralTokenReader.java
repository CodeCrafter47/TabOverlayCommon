package de.codecrafter47.taboverlay.config.expression.token;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;

public class NonQuotedLiteralTokenReader extends TokenReader {

    public NonQuotedLiteralTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        int startIndex = position.getIndex();
        int index = position.getIndex();

        while (++index < text.length() && !Character.isWhitespace(text.charAt(index)))
            ;
        position.setIndex(index);

        return new StringToken(text.substring(startIndex, position.getIndex()));
    }
}
