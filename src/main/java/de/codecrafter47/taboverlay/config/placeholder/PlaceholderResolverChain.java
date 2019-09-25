package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class PlaceholderResolverChain implements PlaceholderResolver<Context>, Cloneable {
    private ArrayList<PlaceholderResolver<Context>> resolvers = new ArrayList<>();

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        for (PlaceholderResolver<Context> resolver : resolvers) {
            try {
                return resolver.resolve(builder, args, tcc);
            } catch (UnknownPlaceholderException ignored) {
            }
        }

        throw new UnknownPlaceholderException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PlaceholderResolverChain clone() {
        PlaceholderResolverChain clone;
        try {
            clone = (PlaceholderResolverChain) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        clone.resolvers = (ArrayList<PlaceholderResolver<Context>>) resolvers.clone();
        return clone;
    }

    public void addResolver(PlaceholderResolver<Context> resolver) {
        resolvers.add(resolver);
    }
}
