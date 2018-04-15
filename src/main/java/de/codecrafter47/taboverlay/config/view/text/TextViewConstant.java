package de.codecrafter47.taboverlay.config.view.text;

import de.codecrafter47.taboverlay.config.context.Context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Text view displaying a constant text.
 */
public class TextViewConstant implements TextView {
    private final String text;

    public TextViewConstant(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void activate(@Nonnull Context context, @Nullable TextViewUpdateListener listener) {
        // nothing to do here
    }

    @Override
    public void deactivate() {
        // nothing to do here
    }
}
