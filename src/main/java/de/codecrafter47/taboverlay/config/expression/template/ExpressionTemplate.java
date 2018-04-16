package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;

public interface ExpressionTemplate {

    ToStringExpression instantiateWithStringResult();

    ToDoubleExpression instantiateWithDoubleResult();

    ToBooleanExpression instantiateWithBooleanResult();

    boolean requiresViewerContext();
}
