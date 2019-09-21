package de.codecrafter47.taboverlay.config.template.ping;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewExpression;

public class ExpressionPingTemplate implements PingTemplate {

    private final ExpressionTemplate expression;

    public ExpressionPingTemplate(ExpressionTemplate expression) {
        this.expression = expression;
    }

    @Override
    public PingView instantiate() {
        return new PingViewExpression(expression.instantiateWithDoubleResult());
    }
}
