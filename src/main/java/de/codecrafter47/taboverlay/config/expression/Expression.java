package de.codecrafter47.taboverlay.config.expression;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.ActiveElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Expression extends ActiveElement {

    /**
     * Activates the expression
     *
     * @param context  the context
     * @param listener the listener
     * @throws IllegalStateException if the element is already active
     */
    void activate(@Nonnull Context context, @Nullable ExpressionUpdateListener listener);
}
