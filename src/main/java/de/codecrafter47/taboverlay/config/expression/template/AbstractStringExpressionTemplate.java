package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.Conversions;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;

public abstract class AbstractStringExpressionTemplate implements ExpressionTemplate {

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        return Conversions.toDouble(instantiateWithStringResult());
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        return Conversions.toBoolean(instantiateWithStringResult());
    }
}
