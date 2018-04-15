package de.codecrafter47.taboverlay.config.view.ping;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.ActiveElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PingView extends ActiveElement {

    int getPing();

    /**
     * Activates the element
     *
     * @param context  the context
     * @param listener the listener
     * @throws IllegalStateException if the element is already active
     */
    void activate(@Nonnull Context context, @Nullable PingViewUpdateListener listener);
}
