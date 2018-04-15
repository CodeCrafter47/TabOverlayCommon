package de.codecrafter47.taboverlay.config.expression.token;

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
    public Token read(ExpressionTokenizer.State state) {
        if (state.input.regionMatches(ignoreCase, state.index, pattern, 0, pattern.length())) {
            state.index += pattern.length();
            return token;
        }
        return null;
    }
}
