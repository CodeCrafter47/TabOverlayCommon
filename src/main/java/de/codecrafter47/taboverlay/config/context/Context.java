package de.codecrafter47.taboverlay.config.context;


import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerSetFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

import javax.annotation.Nullable;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Context implements Cloneable {

    public static Context from(Player viewer, ScheduledExecutorService eventQueue) {
        Context context = new Context();
        context.setViewer(viewer);
        context.setTabEventQueue(eventQueue);
        return context;
    }

    private Object[] customObjects;

    @Getter
    @Setter
    private Player viewer;

    @Getter
    @Setter
    private Player player;

    @Getter
    @Setter
    private ScheduledExecutorService tabEventQueue;

    @Getter
    @Setter
    private PlayerSetFactory playerSetFactory;

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getCustomObject(ContextKey<T> key) {
        if (customObjects != null) {
            for (int i = 0; i < customObjects.length; i += 2) {
                if (key.equals(customObjects[i])) {
                    return (T) customObjects[i + 1];
                }
            }
        }
        return null;
    }

    public <T> void setCustomObject(ContextKey<T> key, T value) {
        if (customObjects == null) {
            customObjects = new Object[]{key, value};
        } else {
            for (int i = 0; i < customObjects.length; i += 2) {
                if (key.equals(customObjects[i])) {
                    customObjects = customObjects.clone();
                    customObjects[i + 1] = value;
                    return;
                }
            }
            val old = customObjects;
            customObjects = new Object[old.length + 2];
            System.arraycopy(old, 0, customObjects, 0, old.length);
            customObjects[old.length] = key;
            customObjects[old.length + 1] = value;
        }
    }

    @Override
    @SneakyThrows
    public Context clone() {
        return (Context) super.clone();
    }
}
