package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.config.context.Context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractActiveElement<L> implements ActiveElement {

    private Context context = null;
    private L listener = null;

    /**
     * Check whether the element is active.
     *
     * @return true if the element is active
     */
    public final boolean isActive() {
        return context != null;
    }

    /**
     * Get the context.
     *
     * @return the context
     * @throws IllegalStateException if the element is not active
     */
    @Nonnull
    public final Context getContext() {
        if (!isActive()) {
            throw new IllegalStateException("not active");
        }
        return context;
    }

    /**
     * Check whether the element has a listener.
     *
     * @return true if the element has a listener
     * @throws IllegalStateException if the element is not active
     */
    public final boolean hasListener() {
        if (!isActive()) {
            throw new IllegalStateException("not active");
        }
        return listener != null;
    }

    /**
     * Get the listener.
     *
     * @return the listener
     * @throws IllegalStateException if the element is not active
     * @throws IllegalStateException if the element has no listener
     */
    @Nonnull
    public final L getListener() {
        if (!isActive()) {
            throw new IllegalStateException("not active");
        }
        if (!hasListener()) {
            throw new IllegalStateException("no listener");
        }
        return listener;
    }

    /**
     * Activates the element
     *
     * @param context the context
     * @param listener the listener
     * @throws IllegalStateException if the element is already active
     */
    public final void activate(@Nonnull Context context, @Nullable L listener) {
        if (isActive()) {
            throw new IllegalStateException("already active");
        }
        this.context = context;
        this.listener = listener;
        onActivation();
    }

    /**
     * Called when the element is activated.
     */
    protected abstract void onActivation();

    /**
     * Deactivated the element.
     * <p>
     * The element must deactivate all children.
     * <p>
     * May never throw an exception.
     */
    @Override
    public final void deactivate() {
        if (!isActive()) {
            return;
        }
        try {
            onDeactivation();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        this.context = null;
        this.listener = null;
    }

    /**
     * Called when the element is deactivated. Should not throw any exceptions.
     * <p>
     * Interactions with the listener element should be avoided.
     */
    protected abstract void onDeactivation();
}
