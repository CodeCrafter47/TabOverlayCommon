package de.codecrafter47.taboverlay.config.expression.operators;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class ListOperator extends Operator {
    public ListOperator(int priority) {
        super(priority);
    }

    public static  ListOperator of(int priority, Function<Collection<ExpressionTemplate>, ExpressionTemplate> function) {
        return new ListOperator(priority) {
            @Override
            public ExpressionTemplate createTemplate(List<ExpressionTemplate> operands) {
                return function.apply(operands);
            }
        };
    }

    @Override
    public ExpressionTemplate createTemplate(ExpressionTemplate a, ExpressionTemplate b) {
        return createTemplate(Arrays.<ExpressionTemplate>asList(a, b));
    }

    public abstract ExpressionTemplate createTemplate(List<ExpressionTemplate> operands);
}
