package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.val;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * A set of {@link TabOverlayProvider}'s.
 * <p>
 * All public methods can be accessed from multiple threads concurrently.
 */
public final class TabOverlayProviderSet {

    /**
     * The parent {@link TabView}.
     */
    private final TabView tabView;

    private final Executor updateExecutor;

    private final Runnable update = this::update;

    private AtomicBoolean updateScheduled = new AtomicBoolean(false);

    /**
     * The list of {@link TabOverlayProvider}'s.
     */
    private final LinkedList<TabOverlayProvider> providers;

    private TabOverlayProvider activeProvider;

    private final TabOverlayHandler tabOverlayHandler;

    private boolean active = true;

    TabOverlayProviderSet(TabView tabView, Executor updateExecutor, TabOverlayHandler tabOverlayHandler) {
        this.tabView = tabView;
        this.updateExecutor = updateExecutor;
        this.tabOverlayHandler = tabOverlayHandler;
        this.providers = new LinkedList<>();
        this.providers.add(this.activeProvider = DefaultTabOverlayProvider.getInstance());
        this.activeProvider.activate(tabView, this.tabOverlayHandler);
    }

    private void setActiveProvider(TabOverlayProvider activeProvider) {
        try {
            this.activeProvider.deactivate(tabView);
        } catch (Throwable th) {
            tabView.getLogger().log(Level.SEVERE, "Failed to deactivate TabOverlayProvider " + activeProvider.getName(), th);
        }
        this.activeProvider = activeProvider;
        try {
            this.activeProvider.activate(tabView, this.tabOverlayHandler);
        } catch (Throwable th) {
            tabView.getLogger().log(Level.SEVERE, "Failed to activate TabOverlayProvider " + activeProvider.getName(), th);
        }
    }

    public synchronized void addProvider(TabOverlayProvider provider) {
        if (!active) {
            return;
        }

        if (providers.stream().anyMatch(p -> p.getName().equals(provider.getName()))) {
            throw new IllegalArgumentException("Duplicate provider name " + provider.getName());
        }

        val iterator = providers.listIterator();

        while (iterator.hasNext()) {
            if (iterator.next().getPriority() < provider.getPriority()) {
                iterator.previous();
                break;
            }
        }

        iterator.add(provider);

        provider.attach(tabView);

        scheduleUpdate();
    }

    public synchronized void removeProvider(TabOverlayProvider provider) {
        if (!active) {
            return;
        }

        if (providers.remove(provider)) {
            provider.deactivate(tabView);
            scheduleUpdate();
        }
    }

    public synchronized void removeProviders(Class<? extends TabOverlayProvider> providerClass) {
        if (!active) {
            return;
        }

        val iterator = providers.iterator();
        while (iterator.hasNext()) {
            val provider = iterator.next();
            if (providerClass.isAssignableFrom(provider.getClass())) {
                provider.detach(tabView);
            }
        }

        providers.removeIf(p -> providerClass.isAssignableFrom(p.getClass()));
        scheduleUpdate();
    }

    public synchronized void removeProvider(final String name) {
        if (!active) {
            return;
        }

        val iterator = providers.iterator();
        while (iterator.hasNext()) {
            val provider = iterator.next();
            if (provider.getName().equals(name)) {
                provider.detach(tabView);
            }
        }

        providers.removeIf(p -> p.getName().equals(name));
        scheduleUpdate();
    }

    public void scheduleUpdate() {
        if (updateScheduled.compareAndSet(false, true)) {
            updateExecutor.execute(update);
        }
    }

    private synchronized void update() {
        if (!active) {
            return;
        }

        updateScheduled.set(false);
        for (TabOverlayProvider provider : providers) {
            boolean shouldActivate = false;
            try {
                shouldActivate = provider.shouldActivate(tabView);
            } catch (Throwable th) {
                tabView.getLogger().log(Level.SEVERE, "Unexpected exception invoking shouldActivate on TabOverlayProvider " + activeProvider.getName(), th);
            }
            if (shouldActivate) {
                if (provider != activeProvider) {
                    setActiveProvider(provider);
                }
                return;
            }
        }
        setActiveProvider(DefaultTabOverlayProvider.getInstance());
    }

    synchronized void deactivate() {
        setActiveProvider(DefaultTabOverlayProvider.getInstance());
        for (TabOverlayProvider provider : providers) {
            if (provider != DefaultTabOverlayProvider.getInstance()) {
                provider.detach(tabView);
            }
        }
        active = false;
    }
}
