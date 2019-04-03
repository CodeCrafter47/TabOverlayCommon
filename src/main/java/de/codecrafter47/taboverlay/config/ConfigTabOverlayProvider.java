package de.codecrafter47.taboverlay.config;

import de.codecrafter47.taboverlay.AbstractPlayerTabOverlayProvider;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.player.GlobalPlayerSetFactory;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerProvider;
import de.codecrafter47.taboverlay.config.player.PlayerSetFactory;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import de.codecrafter47.taboverlay.config.view.TabOverlayView;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledExecutorService;

// todo maybe catch exceptions here? in onAttach, etc.
public class ConfigTabOverlayProvider extends AbstractPlayerTabOverlayProvider {

    private final AbstractTabOverlayTemplate template;
    private final ActivationHandler activationHandler;
    private final Context context;
    private TabOverlayView tabView;

    public ConfigTabOverlayProvider(@Nonnull @NonNull TabView tabView, @Nonnull @NonNull AbstractTabOverlayTemplate template, @Nonnull @NonNull Player viewer, @Nonnull @NonNull ScheduledExecutorService eventQueue, @Nonnull @NonNull PlayerProvider playerProvider, @Nonnull @NonNull GlobalPlayerSetFactory globalPlayerSetFactory) {
        super(tabView, template.getPath().toString(), template.getPriority());
        this.template = template;
        this.activationHandler = new ActivationHandler(template.getViewerPredicate().instantiateWithBooleanResult());
        this.context = Context.from(viewer, eventQueue);
        this.context.setPlayerSetFactory(new PlayerSetFactory(playerProvider, globalPlayerSetFactory, tabView.getLogger(), context));
    }

    @Override
    @SneakyThrows
    protected void onAttach() {
        context.getTabEventQueue().submit(() -> activationHandler.activate(context)).get();
    }

    @Override
    @SneakyThrows
    protected void onActivate(TabOverlayHandler handler) {
        context.getTabEventQueue().submit(() -> tabView = TabOverlayView.create(getTabView(), handler, context, template)).get();
    }

    @Override
    @SneakyThrows
    protected void onDeactivate() {
        context.getTabEventQueue().submit(() -> tabView.deactivate()).get();
    }

    @Override
    @SneakyThrows
    protected void onDetach() {
        context.getTabEventQueue().submit(activationHandler::deactivate).get();
    }

    @Override
    protected boolean shouldActivate() {
        return activationHandler.shouldActivate;
    }

    private class ActivationHandler implements ExpressionUpdateListener, ActiveElement {

        private final ToBooleanExpression predicate;
        private volatile boolean shouldActivate = false;

        private ActivationHandler(ToBooleanExpression predicate) {
            this.predicate = predicate;
        }

        protected void activate(Context context) {
            predicate.activate(context, this);
            shouldActivate = predicate.evaluate();
        }

        @Override
        public void deactivate() {
            predicate.deactivate();
            shouldActivate = false;
        }

        @Override
        public void onExpressionUpdate() {
            boolean shouldActivate = predicate.evaluate();
            if (shouldActivate != this.shouldActivate) {
                this.shouldActivate = shouldActivate;
                getTabView().getTabOverlayProviders().scheduleUpdate();
            }
        }
    }
}
