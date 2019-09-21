package de.codecrafter47.taboverlay.config.expression.token;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class NumberTokenReader extends TokenReader {

    private NumberFormat format = NumberFormat.getInstance(Locale.ROOT);

    public NumberTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(ExpressionTokenizer.State state) {
        ParsePosition parsePosition = new ParsePosition(state.index);
        Number number = format.parse(state.input, parsePosition);
        if (parsePosition.getIndex() != state.index) {
            state.index = parsePosition.getIndex();

            return new NumberToken(number.doubleValue());
        }
        return null;
    }
}
