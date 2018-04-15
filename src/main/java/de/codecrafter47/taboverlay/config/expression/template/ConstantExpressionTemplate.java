package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ConstantExpressionTemplate implements ExpressionTemplate {

    private final ToStringExpression stringExpression;
    private final ToDoubleExpression doubleExpression;
    private final ToBooleanExpression booleanExpression;

    private ConstantExpressionTemplate(String stringValue, double doubleValue, boolean booleanValue) {
        stringExpression = ToStringExpression.literal(stringValue);
        doubleExpression = ToDoubleExpression.literal(doubleValue);
        booleanExpression = ToBooleanExpression.literal(booleanValue);
    }

    public static ConstantExpressionTemplate of(String stringValue) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(stringValue);
        } catch (NumberFormatException ex) {
            doubleValue = stringValue.length();
        }
        return new ConstantExpressionTemplate(stringValue, doubleValue, Boolean.parseBoolean(stringValue));
    }

    public static ConstantExpressionTemplate of(double doubleValue) {
        return new ConstantExpressionTemplate(((int) doubleValue) == doubleValue ? Integer.toString((int) doubleValue) : Double.toString(doubleValue), doubleValue, doubleValue != 0);
    }

    public static ConstantExpressionTemplate of(boolean booleanValue) {
        return new ConstantExpressionTemplate(Boolean.toString(booleanValue), booleanValue ? 1 : 0, booleanValue);
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return stringExpression;
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        return doubleExpression;
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        return booleanExpression;
    }

    @Override
    public boolean requiresViewerContext() {
        return false;
    }
}
