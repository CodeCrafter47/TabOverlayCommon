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

package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Data
public abstract class AbstractTabOverlayTemplate {

    private Path path;

    private ExpressionTemplate viewerPredicate;

    private int priority;

    @Nullable
    private List<TextTemplate> header;

    private float headerAnimationUpdateInterval = Float.NaN;

    @Nullable
    private List<TextTemplate> footer;

    private float footerAnimationUpdateInterval = Float.NaN;

    private Map<String, PlayerSetTemplate> playerSets;

    public boolean showHeaderAndFooter() {
        return getHeader() != null || getFooter() != null;
    }

    public abstract AbstractActiveElement<?> createContentView(TabView tabView, TabOverlayHandler handler);

}
