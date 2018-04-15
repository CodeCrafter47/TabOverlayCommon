package de.codecrafter47.taboverlay.config.template.text;

import de.codecrafter47.taboverlay.config.view.text.TextViewConstant;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;

import javax.annotation.Nonnull;

/**
 * A text template displaying constant text.
 */
public class ConstantTextTemplate implements TextTemplate {

    @Nonnull
    @NonNull
    private final TextViewConstant view;

    public ConstantTextTemplate(String text) {
        view = new TextViewConstant(text);
    }
    @Override
    @Nonnull
    @NonNull
    public TextView instantiate() {
        return view;
    }
}
