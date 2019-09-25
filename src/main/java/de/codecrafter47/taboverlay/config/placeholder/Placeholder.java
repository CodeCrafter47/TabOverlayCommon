package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.text.TextView;

import javax.annotation.Nonnull;

public interface Placeholder extends TextTemplate, ExpressionTemplate {

    Placeholder DUMMY = new Placeholder() {
        @Override
        public ToStringExpression instantiateWithStringResult() {
            return ToStringExpression.literal("");
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return ToDoubleExpression.literal(0.0);
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return ToBooleanExpression.literal(false);
        }

        @Override
        public boolean requiresViewerContext() {
            return false;
        }

        @Nonnull
        @Override
        public TextView instantiate() {
            return TextView.EMPTY;
        }
    };

}
