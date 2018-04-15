package de.codecrafter47.taboverlay;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;

@Value
public class Icon implements Serializable {
    private static final long serialVersionUID = -8251683111229590559L;

    public static final Icon DEFAULT_STEVE = new Icon(null, true, false);
    public static final Icon DEFAULT_ALEX = new Icon(null, false, true);

    private final ProfileProperty textureProperty;
    private final boolean steve;
    private final boolean alex;

    private Icon(ProfileProperty textureProperty, boolean steve, boolean alex) {
        this.textureProperty = textureProperty;
        this.steve = steve;
        this.alex = alex;
    }

    public Icon(@Nonnull @NonNull ProfileProperty textureProperty) {
        this(textureProperty, false, false);
    }

    public boolean hasTextureProperty() {
        return textureProperty != null;
    }
}
