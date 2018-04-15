package de.codecrafter47.taboverlay.config.expression.token;

import lombok.Getter;

public abstract class TokenReader {
    @Getter
    private final int priority;

    public TokenReader(int priority) {
        this.priority = priority;
    }

    public abstract Token read(ExpressionTokenizer.State state);

}
