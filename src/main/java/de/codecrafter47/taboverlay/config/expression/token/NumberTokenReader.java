package de.codecrafter47.taboverlay.config.expression.token;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class NumberTokenReader extends TokenReader {

    private NumberFormat format = NumberFormat.getInstance(Locale.ROOT);

    public NumberTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {
        int previous = position.getIndex();
        Number number = format.parse(text, position);
        if (position.getIndex() != previous) {
            return new NumberToken(number.doubleValue());
        }
        return null;
    }
}
