/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.TabOverlayHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A set of {@link TabOverlayProvider}'s.
 * <p>
 * All public methods can be accessed from multiple threads concurrently.
 */
public final class TabOverlayProviderSet {

    private static final Comparator<TabOverlayProvider> PRIORITY_COMPARATOR = Comparator.comparingInt(TabOverlayProvider::getPriority).reversed();

    /**
     * The parent {@link TabView}.
     */
    private final TabView tabView;

    private final Executor updateExecutor;

    private final Runnable update = this::update;

    private final AtomicBoolean updateScheduled = new AtomicBoolean(false);

    /**
     * The list of {@link TabOverlayProvider}'s.
     */
    private final List<TabOverlayProvider> providers;

    @Nullable
    private TabOverlayProvider activeProvider;

    @Nullable
    private TabOverlayHandler tabOverlayHandler;

    private boolean active = true;

    TabOverlayProviderSet(TabView tabView, Executor updateExecutor) {
        this.tabView = tabView;
        this.updateExecutor = updateExecutor;
        this.providers = new ArrayList<>();
    }

    private synchronized void setActiveProvider(@Nonnull TabOverlayProvider provider) {
        if (!active) {
            return;
        }
        if (this.activeProvider != null) {
            try {
                this.activeProvider.deactivate(tabView);
            } catch (Throwable th) {
                tabView.getLogger().log(Level.SEVERE, "Failed to deactivate TabOverlayProvider " + provider.getName(), th);
            }
        }
        this.activeProvider = provider;
        try {
            this.activeProvider.activate(tabView, this.tabOverlayHandler);
        } catch (Throwable th) {
            tabView.getLogger().log(Level.SEVERE, "Failed to activate TabOverlayProvider " + provider.getName(), th);
        }
    }

    private synchronized void update() {
        if (tabOverlayHandler == null) {
            return;
        }

        updateScheduled.set(false);
        for (TabOverlayProvider provider : providers) {
            boolean shouldActivate = false;
            try {
                shouldActivate = provider.shouldActivate(tabView);
            } catch (Throwable th) {
                tabView.getLogger().log(Level.SEVERE, "Unexpected exception invoking shouldActivate on TabOverlayProvider " + provider.getName(), th);
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

    public synchronized void addProviders(Collection<TabOverlayProvider> providers) {
        if (!active) {
            return;
        }
        ArrayList<TabOverlayProvider> tabOverlayProviders = new ArrayList<>(providers);
        Set<String> names = tabOverlayProviders.stream().map(TabOverlayProvider::getName).collect(Collectors.toSet());
        for (TabOverlayProvider provider : this.providers) {
            if (names.contains(provider.getName())) {
                throw new IllegalArgumentException("Duplicate provider name " + provider.getName());
            }
        }

        this.providers.addAll(tabOverlayProviders);
        this.providers.sort(PRIORITY_COMPARATOR);

        updateExecutor.execute(() -> {
            for (TabOverlayProvider provider : tabOverlayProviders) {
                provider.attach(tabView);
            }
            update();
        });
    }

    public synchronized void addProvider(TabOverlayProvider provider) {
        if (!active) {
            return;
        }
        if (providers.stream().anyMatch(p -> p.getName().equals(provider.getName()))) {
            throw new IllegalArgumentException("Duplicate provider name " + provider.getName());
        }

        providers.add(provider);
        providers.sort(PRIORITY_COMPARATOR);

        updateExecutor.execute(() -> {
            provider.attach(tabView);
            update();
        });
    }

    public synchronized void removeProvider(TabOverlayProvider provider) {
        if (!active) {
            return;
        }
        if (providers.remove(provider)) {
            updateExecutor.execute(() -> {
                update();
                provider.detach(tabView);
            });
        }
    }

    public synchronized TabOverlayProvider getActiveProvider() {
        return activeProvider;
    }

    public synchronized void removeProviders(Class<? extends TabOverlayProvider> providerClass) {
        if (!active) {
            return;
        }

        List<TabOverlayProvider> toRemove = this.providers.stream()
                .filter(p -> providerClass.isAssignableFrom(p.getClass()))
                .collect(Collectors.toList());

        providers.removeIf(p -> providerClass.isAssignableFrom(p.getClass()));

        updateExecutor.execute(() -> {
            update();
            for (TabOverlayProvider provider : toRemove) {
                provider.detach(tabView);
            }
        });
    }

    public synchronized void removeProvider(final String name) {
        if (!active) {
            return;
        }
        for (TabOverlayProvider provider : providers) {
            if (provider.getName().equals(name)) {
                removeProvider(provider);
                return;
            }
        }
    }

    public void scheduleUpdate() {
        if (!active) {
            return;
        }
        if (updateScheduled.compareAndSet(false, true)) {
            updateExecutor.execute(update);
        }
    }

    public synchronized void activate(TabOverlayHandler handler) {
        this.tabOverlayHandler = handler;
        scheduleUpdate();
    }

    synchronized void deactivate() {
        updateExecutor.execute(() -> {
            synchronized (TabOverlayProviderSet.this) {
                for (TabOverlayProvider provider : providers) {
                    if (provider != DefaultTabOverlayProvider.getInstance()) {
                        provider.detach(tabView);
                    }
                }
                update();
                active = false;
            }
        });
    }
}
