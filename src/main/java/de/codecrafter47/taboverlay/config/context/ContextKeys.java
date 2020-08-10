package de.codecrafter47.taboverlay.config.context;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;

public class ContextKeys {

    public static final ContextKey<Integer> OTHER_COUNT = new ContextKey<>("OTHER_COUNT");
    public static final ContextKey<ExpressionTemplate> BAR_VALUE = new ContextKey<>("BAR_VALUE");
    public static final ContextKey<ExpressionTemplate> BAR_PERCENTAGE = new ContextKey<>("BAR_PERCENTAGE");
    public static final ContextKey<ExpressionTemplate> BAR_MIN_VALUE = new ContextKey<>("BAR_MIN_VALUE");
    public static final ContextKey<ExpressionTemplate> BAR_MAX_VALUE = new ContextKey<>("BAR_MAX_VALUE");
}
