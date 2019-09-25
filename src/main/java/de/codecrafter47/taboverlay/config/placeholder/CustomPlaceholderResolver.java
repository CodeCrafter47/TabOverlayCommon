package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class CustomPlaceholderResolver implements PlaceholderResolver<Context> {

    private final Map<String, CustomPlaceholderConfiguration> customPlaceholderMap;

    public CustomPlaceholderResolver(Map<String, CustomPlaceholderConfiguration> customPlaceholderMap) {
        this.customPlaceholderMap = customPlaceholderMap;
    }

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text) {
            String placeholderId = ((PlaceholderArg.Text) args.get(0)).getValue();
            CustomPlaceholderConfiguration customPlaceholder = customPlaceholderMap.get(placeholderId);
            if (customPlaceholder == null) {
                throw new UnknownPlaceholderException();
            }
            if (tcc.hasVisitedCustomPlaceholder(placeholderId)) {
                throw new PlaceholderException("Custom placeholder recursion");
            }
            TemplateCreationContext childContext = tcc.clone();
            childContext.visitCustomPlaceholder(placeholderId);
            args.remove(0);
            PlaceholderBuilder<?, ?> result = customPlaceholder.bindArgs(builder, args, childContext);
            if (customPlaceholder.getParameters().getValue() != 0) {
                args.clear();
            }
            return result;
        }
        throw new UnknownPlaceholderException();
    }
}
