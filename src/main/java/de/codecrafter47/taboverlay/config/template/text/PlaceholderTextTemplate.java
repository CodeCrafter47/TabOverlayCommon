package de.codecrafter47.taboverlay.config.template.text;

import de.codecrafter47.taboverlay.config.placeholder.Placeholder;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A placeholder text template.
 */
public class PlaceholderTextTemplate implements TextTemplate {

    private final Placeholder placeholder;

    public PlaceholderTextTemplate(Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    @Nonnull
    @NonNull
    public TextView instantiate() {
        return Objects.requireNonNull(placeholder.instantiate());
    }
}
