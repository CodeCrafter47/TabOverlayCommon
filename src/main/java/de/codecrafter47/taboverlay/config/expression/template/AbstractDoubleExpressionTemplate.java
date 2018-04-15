package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.Conversions;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;

public abstract class AbstractDoubleExpressionTemplate implements ExpressionTemplate {
    @Override
    public ToStringExpression instantiateWithStringResult() {
        return Conversions.toString(instantiateWithDoubleResult());
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        return Conversions.toBoolean(instantiateWithDoubleResult());
    }
}
