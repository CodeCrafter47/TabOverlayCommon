package de.codecrafter47.taboverlay.config.view.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import lombok.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IconViewConstant implements IconView {

    private final Icon icon;

    public IconViewConstant(@NonNull @Nonnull Icon icon) {
        this.icon = icon;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void activate(@Nonnull Context context, @Nullable IconViewUpdateListener listener) {
        // nothing to do here
    }

    @Override
    public void deactivate() {
        // nothing to do here
    }
}
