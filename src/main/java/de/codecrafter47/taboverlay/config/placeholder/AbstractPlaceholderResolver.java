package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Setter;
import lombok.val;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractPlaceholderResolver<C> implements PlaceholderResolver<C> {
    private Map<String, PlaceholderResolver<C>> placeholders = new HashMap<>();
    @Setter
    private Function<PlaceholderBuilder<C, ?>, PlaceholderBuilder<C, ?>> defaultPlaceholder;

    public AbstractPlaceholderResolver() {
    }

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<C, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (args.size() == 0) {
            if (defaultPlaceholder != null) {
                return defaultPlaceholder.apply(builder);
            }
            throw new UnknownPlaceholderException();
        } else {
            String token = args.get(0).getText();

            val placeholderResolver = placeholders.get(token);
            if (placeholderResolver == null) {
                throw new UnknownPlaceholderException();
            }

            if (!args.isEmpty()) {
                args.remove(0);
            }

            return placeholderResolver.resolve(builder, args, tcc);
        }
    }

    protected final <R, T> void addPlaceholder(String name, PlaceholderResolver<C> resolver) {
        placeholders.put(name, resolver);
    }

}
