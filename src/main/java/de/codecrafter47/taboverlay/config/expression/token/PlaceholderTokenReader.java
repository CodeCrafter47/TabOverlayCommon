package de.codecrafter47.taboverlay.config.expression.token;

public class PlaceholderTokenReader extends TokenReader {
    public PlaceholderTokenReader(int priority) {
        super(priority);
    }

    @Override
    public Token read(ExpressionTokenizer.State state) {
        if (state.index + 1 < state.input.length() && state.input.charAt(state.index) == '$' && state.input.charAt(state.index + 1) == '{') {
            int startIndex = state.index;

            // search for closing parenthesis
            while (state.index < state.input.length() && '}' != state.input.charAt(state.index)) {
                state.index += 1;
            }

            return new PlaceholderToken(state.input.substring(startIndex + 2, state.index++));
        }
        return null;
    }
}
