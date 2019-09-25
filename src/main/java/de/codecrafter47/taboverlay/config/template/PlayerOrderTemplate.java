package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderDataProviderSupplier;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import java.util.List;

@Value
public class PlayerOrderTemplate {
    @Nonnull
    @NonNull
    List<Entry> entries;

    public boolean requiresViewerContext() {
        for (Entry entry : entries) {
            if (entry.requiresViewerContext())
                return true;
        }
        return false;
    }

    @Value
    public static class Entry {
        @Nonnull
        @NonNull
        PlayerPlaceholderDataProviderSupplier<?, ?> placeholder;

        @Nonnull
        @NonNull
        Direction direction;

        @Nonnull
        @NonNull
        Type type;

        boolean requiresViewerContext() {
            return direction == Direction.VIEWER_FIRST;
        }
    }

    public enum Direction {
        ASCENDING, DESCENDING, VIEWER_FIRST
    }

    public enum Type {
        TEXT, NUMBER
    }
}
