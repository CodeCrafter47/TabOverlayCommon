package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.Conversions;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;

public abstract class AbstractBooleanExpressionTemplate implements ExpressionTemplate {

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return Conversions.toString(instantiateWithBooleanResult());
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        return Conversions.toDouble(instantiateWithBooleanResult());
    }
}
