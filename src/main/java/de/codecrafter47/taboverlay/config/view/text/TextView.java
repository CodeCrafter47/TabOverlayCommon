package de.codecrafter47.taboverlay.config.view.text;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TextView extends ActiveElement {

    TextView EMPTY = new TextView() {
        @Override
        public String getText() {
            return "";
        }

        @Override
        public void activate(@Nonnull Context context, @Nullable TextViewUpdateListener listener) {
            // nothing to do here
        }

        @Override
        public void deactivate() {
            // nothing to do here
        }
    };

    String getText();

    /**
     * Activates the element
     *
     * @param context  the context
     * @param listener the listener
     * @throws IllegalStateException if the element is already active
     */
    void activate(@Nonnull Context context, @Nullable TextViewUpdateListener listener);
}
