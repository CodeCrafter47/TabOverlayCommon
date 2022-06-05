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

import de.codecrafter47.taboverlay.config.dsl.PlayerSetConfiguration;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;

@Value
public class PlayerSetTemplate {

    @Nonnull
    @NonNull
    private PlayerSetConfiguration.Visibility hiddenPlayersVisibility;

    @Nonnull
    @NonNull
    private ExpressionTemplate predicate;

    private boolean requiresViewerContext;

    @Builder
    public PlayerSetTemplate(@Nonnull PlayerSetConfiguration.Visibility hiddenPlayersVisibility, @Nonnull ExpressionTemplate predicate) {
        this.hiddenPlayersVisibility = hiddenPlayersVisibility;
        this.predicate = predicate;
        this.requiresViewerContext = predicate.requiresViewerContext() || hiddenPlayersVisibility == PlayerSetConfiguration.Visibility.VISIBLE_TO_ADMINS;
    }
}
