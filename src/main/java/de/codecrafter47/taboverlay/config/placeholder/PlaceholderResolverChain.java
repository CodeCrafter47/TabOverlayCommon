package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import java.util.ArrayList;

public final class PlaceholderResolverChain implements PlaceholderResolver, Cloneable {
    private ArrayList<PlaceholderResolver> resolvers = new ArrayList<>();

    @Override
    public Placeholder resolve(String[] value, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        for (PlaceholderResolver resolver : resolvers) {
            try {
                return resolver.resolve(value, tcc);
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
        clone.resolvers = (ArrayList<PlaceholderResolver>) resolvers.clone();
        return clone;
    }

    public void addResolver(PlaceholderResolver resolver) {
        resolvers.add(resolver);
    }
}
