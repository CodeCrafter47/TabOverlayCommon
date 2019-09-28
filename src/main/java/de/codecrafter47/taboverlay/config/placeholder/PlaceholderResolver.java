package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.util.List;

@FunctionalInterface
public interface PlaceholderResolver<C> {

    /**
     * Resolves a placeholder
     *
     * @param builder
     * @param args placeholder (without ${})
     * @param tcc
     * @return the placeholder
     * @throws UnknownPlaceholderException if the placeholder cannot be resolved
     */
    @Nonnull
    PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<C, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException;
}
