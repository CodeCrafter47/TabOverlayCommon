package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PlaceholderArg {

    private PlaceholderArg() {

    }

    public abstract String getText();

    public abstract ExpressionTemplate getExpression();

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class Text extends PlaceholderArg {

        String value;

        @Override
        public String getText() {
            return value;
        }

        @Override
        public ExpressionTemplate getExpression() {
            return ConstantExpressionTemplate.of(value);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class Placeholder extends PlaceholderArg {

        de.codecrafter47.taboverlay.config.placeholder.Placeholder value;
        String text;

        @Override
        public ExpressionTemplate getExpression() {
            return value;
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class Complex extends PlaceholderArg {

        List<PlaceholderArg> value;

        @Override
        public String getText() {
            StringBuilder result = new StringBuilder();
            for (PlaceholderArg arg : value) {
                result.append(arg.getText());
            }
            return result.toString();
        }

        @Override
        public ExpressionTemplate getExpression() {
            return ExpressionTemplates.concat(value.stream().map(PlaceholderArg::getExpression).collect(Collectors.toList()));
        }
    }
}
