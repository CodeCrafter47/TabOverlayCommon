package de.codecrafter47.taboverlay.config.view.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.ActiveElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IconView extends ActiveElement {

    Icon getIcon();

    /**
     * Activates the element
     *
     * @param context  the context
     * @param listener the listener
     * @throws IllegalStateException if the element is already active
     */
    void activate(@Nonnull Context context, @Nullable IconViewUpdateListener listener);
}
