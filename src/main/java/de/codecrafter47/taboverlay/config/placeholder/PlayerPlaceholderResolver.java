package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import java.util.Arrays;

public class PlayerPlaceholderResolver implements PlaceholderResolver {

    private final AbstractPlayerPlaceholderResolver delegate;
    private final PlayerPlaceholder.BindPoint bindPoint;
    private final String prefix;

    public PlayerPlaceholderResolver(AbstractPlayerPlaceholderResolver delegate, PlayerPlaceholder.BindPoint bindPoint) {
        this.delegate = delegate;
        this.bindPoint = bindPoint;
        this.prefix = bindPoint == PlayerPlaceholder.BindPoint.PLAYER ? "player" : "viewer";
    }

    @Override
    public Placeholder resolve(String[] tokens, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {

        if (tokens.length < 1) {
            throw new UnknownPlaceholderException();
        }
        if (!prefix.equals(tokens[0])) {
            throw new UnknownPlaceholderException();
        }

        if (bindPoint == PlayerPlaceholder.BindPoint.VIEWER && !tcc.isViewerAvailable()) {
            throw new PlaceholderException("viewer bound placeholders not available in the current context");
        } else if (bindPoint == PlayerPlaceholder.BindPoint.PLAYER && !tcc.isPlayerAvailable()) {
            StringBuilder suggest = new StringBuilder("viewer");
            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i];
                suggest.append(' ');
                suggest.append(token);
            }
            throw new PlaceholderException("player bound placeholders not available in the current context\n"
            + "Note: You might want to use ${" + suggest + "} instead");
        }

        return delegate.resolve(bindPoint, Arrays.copyOfRange(tokens, 1, tokens.length));
    }
}
