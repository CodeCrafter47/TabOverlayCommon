package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.expression.*;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerSet;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

// todo allow format
public class PlayerSetPlaceholderResolver implements PlaceholderResolver {

    @Override
    public Placeholder resolve(String[] value, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (value.length >= 1 && value[0].startsWith("playerset:")) {
            String playerSetId = value[0].substring(10);
            PlayerSetTemplate playerSetTemplate = tcc.getPlayerSets().get(playerSetId);
            if (playerSetTemplate == null) {
                throw new PlaceholderException("Unknown player set " + playerSetId);
            }
            return new PlayerCountPlaceholder(playerSetTemplate);
        }
        throw new UnknownPlaceholderException();
    }

    private static class PlayerCountPlaceholder implements Placeholder {

        private final PlayerSetTemplate playerSetTemplate;

        private PlayerCountPlaceholder(PlayerSetTemplate playerSetTemplate) {
            this.playerSetTemplate = playerSetTemplate;
        }

        @Override
        public ToStringExpression instantiateWithStringResult() {
            return new ToStringInstance(playerSetTemplate);
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return new ToDoubleInstance(playerSetTemplate);
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Conversions.toBoolean(instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return true; // todo too lazy to check, playing safe
        }

        @Override
        public TextView instantiate() {
            return new TextViewInstance(playerSetTemplate);
        }

        private static abstract class AbstractInstance<T> extends AbstractActiveElement<T> implements PlayerSet.Listener {

            private final PlayerSetTemplate playerSetTemplate;
            private PlayerSet playerSet;

            private AbstractInstance(PlayerSetTemplate playerSetTemplate) {
                this.playerSetTemplate = playerSetTemplate;
            }

            @Override
            protected void onActivation() {
                playerSet = getContext().getPlayerSetFactory().getInstance(playerSetTemplate);
                playerSet.addListener(this);
            }

            @Override
            protected void onDeactivation() {
                playerSet.removeListener(this);
            }

            protected final int getPlayerCount() {
                return playerSet.getCount();
            }

            @Override
            public void onPlayerAdded(Player player) {
                notifyListener();
            }

            @Override
            public void onPlayerRemoved(Player player) {
                notifyListener();
            }

            protected abstract void notifyListener();
        }

        private static class ToDoubleInstance extends AbstractInstance<ExpressionUpdateListener> implements ToDoubleExpression {

            private ToDoubleInstance(PlayerSetTemplate playerSetTemplate) {
                super(playerSetTemplate);
            }

            @Override
            protected void notifyListener() {
                if (hasListener()) {
                    getListener().onExpressionUpdate();
                }
            }

            @Override
            public double evaluate() {
                return getPlayerCount();
            }
        }

        private static class ToStringInstance extends AbstractInstance<ExpressionUpdateListener> implements ToStringExpression {

            private ToStringInstance(PlayerSetTemplate playerSetTemplate) {
                super(playerSetTemplate);
            }

            @Override
            protected void notifyListener() {
                if (hasListener()) {
                    getListener().onExpressionUpdate();
                }
            }

            @Override
            public String evaluate() {
                return Integer.toString(getPlayerCount());
            }
        }

        private static class TextViewInstance extends AbstractInstance<TextViewUpdateListener> implements TextView {

            private TextViewInstance(PlayerSetTemplate playerSetTemplate) {
                super(playerSetTemplate);
            }

            @Override
            protected void notifyListener() {
                if (hasListener()) {
                    getListener().onTextUpdated();
                }
            }

            @Override
            public String getText() {
                return Integer.toString(getPlayerCount());
            }
        }
    }
}
