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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigTabOverlayProvider extends AbstractPlayerTabOverlayProvider {

    private final AbstractTabOverlayTemplate template;
    private final ActivationHandler activationHandler;
    private final Logger logger;
    private final Context context;
    private TabOverlayView tabOverlayView;

    ConfigTabOverlayProvider(@Nonnull @NonNull TabView tabView, @Nonnull @NonNull AbstractTabOverlayTemplate template, @Nonnull @NonNull Player viewer, @Nonnull @NonNull ScheduledExecutorService eventQueue, @Nonnull @NonNull PlayerProvider playerProvider, @Nonnull @NonNull GlobalPlayerSetFactory globalPlayerSetFactory, @Nonnull @NonNull Logger logger) {
        super(tabView, template.getPath().toString(), template.getPriority());
        this.template = template;
        this.activationHandler = new ActivationHandler(template.getViewerPredicate().instantiateWithBooleanResult());
        this.logger = logger;
        this.context = Context.from(viewer, eventQueue);
        this.context.setPlayerSetFactory(new PlayerSetFactory(playerProvider, globalPlayerSetFactory, tabView.getLogger(), context));
    }

    @Override
    @SneakyThrows
    protected void onAttach() {
        context.getTabEventQueue().submit(() -> {
            try {
                activationHandler.activate(context);
            } catch (Throwable th) {
                logger.log(Level.SEVERE, "Failed to activate activationHandler for " + template.getPath().toString(), th);
            }
        }).get();
    }

    @Override
    @SneakyThrows
    protected void onActivate(TabOverlayHandler handler) {
        context.getTabEventQueue().submit(() -> {
            try {
                tabOverlayView = TabOverlayView.create(getTabView(), handler, context, template);
            } catch (Throwable th) {
                logger.log(Level.SEVERE, "Failed to activate tab overlay " + template.getPath().toString(), th);
            }
        }).get();
    }

    @Override
    @SneakyThrows
    protected void onDeactivate() {
        try {
            context.getTabEventQueue().submit(() -> {
                try {
                    if (tabOverlayView != null) {
                        tabOverlayView.deactivate();
                    }
                } catch (Throwable th) {
                    logger.log(Level.SEVERE, "Failed to deactivate tab overlay " + template.getPath().toString(), th);
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @SneakyThrows
    protected void onDetach() {
        try {
            context.getTabEventQueue().submit(() -> {
                try {
                    activationHandler.deactivate();
                } catch (Throwable th) {
                    logger.log(Level.SEVERE, "Failed to deactivate activationHandler for " + template.getPath().toString(), th);
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
