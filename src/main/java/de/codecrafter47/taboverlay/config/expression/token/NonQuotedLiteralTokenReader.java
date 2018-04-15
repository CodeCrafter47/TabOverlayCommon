package de.codecrafter47.taboverlay.config.expression.token;

public class NonQuotedLiteralTokenReader extends TokenReader {

    public NonQuotedLiteralTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(ExpressionTokenizer.State state) {
        int startIndex = state.index;

        while (++state.index < state.input.length() && !Character.isWhitespace(state.input.charAt(state.index)))
            ;

        return new StringToken(state.input.substring(startIndex, state.index++));
    }
}
