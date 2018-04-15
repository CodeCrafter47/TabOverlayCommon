package de.codecrafter47.taboverlay.config.expression.operators;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public abstract class Operator {
    @Getter
    private final int priority;

    public static  Operator of(int priority, BiFunction<ExpressionTemplate, ExpressionTemplate, ExpressionTemplate> function) {
        return new Operator(priority) {
            @Override
            public ExpressionTemplate createTemplate(ExpressionTemplate a, ExpressionTemplate b) {
                return function.apply(a, b);
            }
        };
    }

    public abstract ExpressionTemplate createTemplate(ExpressionTemplate a, ExpressionTemplate b);
}
