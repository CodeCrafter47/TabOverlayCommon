package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.ps.PSExpressionUnavailableException;
import de.codecrafter47.taboverlay.config.expression.ps.PSToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ps.PSToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ps.PSToStringExpression;

public interface ExpressionTemplate {

    ToStringExpression instantiateWithStringResult();

    ToDoubleExpression instantiateWithDoubleResult();

    ToBooleanExpression instantiateWithBooleanResult();

    boolean requiresViewerContext();
}
