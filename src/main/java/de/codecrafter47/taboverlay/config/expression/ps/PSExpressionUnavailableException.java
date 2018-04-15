package de.codecrafter47.taboverlay.config.expression.ps;

public final class PSExpressionUnavailableException extends Exception {
    private static final long serialVersionUID = 7329437929981749816L;

    public static final PSExpressionUnavailableException INSTANCE = new PSExpressionUnavailableException();

    private PSExpressionUnavailableException() {
    }
}
