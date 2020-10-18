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

package de.codecrafter47.taboverlay.config.context;


import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerSetFactory;
import lombok.*;

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
