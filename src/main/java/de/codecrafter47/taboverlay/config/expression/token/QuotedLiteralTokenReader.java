package de.codecrafter47.taboverlay.config.expression.token;

public class QuotedLiteralTokenReader extends TokenReader {
    private final char quote;

    public QuotedLiteralTokenReader(int priority, char quote) {
        super(priority);
        this.quote = quote;
    }

    @Override
    public Token read(ExpressionTokenizer.State state) {
        if (state.index < state.input.length() && state.input.charAt(state.index) == quote) {
            int startIndex = state.index;

            while (++state.index < state.input.length() && quote != state.input.charAt(state.index))
                ;

            return new StringToken(state.input.substring(startIndex + 1, state.index++));
        }
        return null;
    }
}
