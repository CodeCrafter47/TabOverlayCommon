package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.dsl.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import java.util.Arrays;
import java.util.Map;

public class CustomPlaceholderResolver implements PlaceholderResolver {

    private final Map<String, CustomPlaceholderConfiguration> customPlaceholderMap;

    public CustomPlaceholderResolver(Map<String, CustomPlaceholderConfiguration> customPlaceholderMap) {
        this.customPlaceholderMap = customPlaceholderMap;
    }

    @Override
    public Placeholder resolve(String[] token, TemplateCreationContext tcc) throws UnknownPlaceholderException {
        CustomPlaceholderConfiguration customPlaceholder = customPlaceholderMap.get(token[0]);
        if (customPlaceholder == null) {
            throw new UnknownPlaceholderException();
        }
        if (tcc.hasVisitedCustomPlaceholder(token[0])) {
            tcc.getErrorHandler().addError("Custom placeholder recursion", customPlaceholder.getStartMark());
            return null; // todo better dummy value
        }
        TemplateCreationContext childContext = tcc.clone();
        childContext.visitCustomPlaceholder(token[0]);
        return customPlaceholder.bindArgs(childContext, Arrays.copyOfRange(token, 1, token.length));
    }
}
