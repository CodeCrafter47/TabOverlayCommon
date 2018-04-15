package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.config.dsl.PlayerSetConfiguration;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;

@Value
public class PlayerSetTemplate {

    @Nonnull
    @NonNull
    private PlayerSetConfiguration.Visibility hiddenPlayersVisibility;

    @Nonnull
    @NonNull
    private ExpressionTemplate predicate;

    private boolean requiresViewerContext;

    @Builder
    public PlayerSetTemplate(@Nonnull PlayerSetConfiguration.Visibility hiddenPlayersVisibility, @Nonnull ExpressionTemplate predicate) {
        this.hiddenPlayersVisibility = hiddenPlayersVisibility;
        this.predicate = predicate;
        this.requiresViewerContext = predicate.requiresViewerContext();
    }
}
