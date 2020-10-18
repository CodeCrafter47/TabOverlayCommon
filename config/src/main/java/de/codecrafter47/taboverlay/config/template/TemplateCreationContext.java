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

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.SortingRulePreprocessor;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.config.dsl.PlayerSetConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.BasicComponentConfiguration;
import de.codecrafter47.taboverlay.config.expression.ExpressionEngine;
import de.codecrafter47.taboverlay.config.icon.IconManager;
import de.codecrafter47.taboverlay.config.misc.Unchecked;
import de.codecrafter47.taboverlay.config.placeholder.AbstractPlaceholderResolver;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderResolver;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderResolverChain;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.template.component.BasicComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ListComponentTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.*;

@Data
public class TemplateCreationContext implements Cloneable {

    private final ExpressionEngine expressionEngine;

    private final IconManager iconManager;

    private final DataKey<Icon> playerIconDataKey;

    private final DataKey<Integer> playerPingDataKey;

    private final ErrorHandler errorHandler;

    private final SortingRulePreprocessor sortingRulePreprocessor;

    private Map<String, CustomPlaceholderConfiguration> customPlaceholders;

    private Map<String, PlayerSetTemplate> playerSets;

    private TextTemplate defaultText;

    private PingTemplate defaultPing;

    private IconTemplate defaultIcon;

    private PlaceholderResolverChain placeholderResolverChain;

    private AbstractPlaceholderResolver<Player> playerPlaceholderResolver;

    private PlayerSetConfiguration.Visibility defaultHiddenPlayerVisibility = PlayerSetConfiguration.Visibility.VISIBLE_TO_ADMINS;

    private BasicComponentConfiguration.LongTextBehaviour defaultLongTextBehaviour = null;

    private boolean viewerAvailable = false;

    private boolean playerAvailable = false;

    private int columns = -1;

    // used for recursion detection
    private HashSet<String> visitedCustomPlaceholders = new HashSet<>();

    public OptionalInt getColumns() {
        return columns != -1 ? OptionalInt.of(columns) : OptionalInt.empty();
    }

    public Optional<BasicComponentConfiguration.LongTextBehaviour> getDefaultLongTextBehaviour() {
        return Optional.ofNullable(defaultLongTextBehaviour);
    }

    public ComponentTemplate emptySlot() {
        return BasicComponentTemplate.builder()
                .icon(defaultIcon)
                .leftText(defaultText)
                .ping(defaultPing)
                .build();
    }

    public ComponentTemplate emptyComponent() {
        // TODO optimize
        return ListComponentTemplate.builder()
                .components(Collections.emptyList())
                .columns(this.getColumns().orElse(1))
                .defaultIcon(this.getDefaultIcon())
                .defaultText(this.getDefaultText())
                .defaultPing(this.getDefaultPing())
                .build();
    }

    public void addPlaceholderResolver(@Nonnull @NonNull PlaceholderResolver<Context> resolver) {
        this.placeholderResolverChain = placeholderResolverChain.clone();
        this.placeholderResolverChain.addResolver(resolver);
    }

    public boolean hasVisitedCustomPlaceholder(@Nonnull @NonNull String id) {
        return this.visitedCustomPlaceholders.contains(id);
    }

    public void visitCustomPlaceholder(@Nonnull @NonNull String id) {
        this.visitedCustomPlaceholders = Unchecked.cast(this.visitedCustomPlaceholders.clone());
        this.visitedCustomPlaceholders.add(id);
    }

    @Override
    public TemplateCreationContext clone() {
        TemplateCreationContext clone;
        try {
            clone = (TemplateCreationContext) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        return clone;
    }
}
