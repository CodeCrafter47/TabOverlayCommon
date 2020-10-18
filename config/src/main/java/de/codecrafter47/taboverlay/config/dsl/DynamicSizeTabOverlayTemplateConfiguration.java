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

package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.template.DynamicSizeTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.icon.PlayerIconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PlayerPingTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamicSizeTabOverlayTemplateConfiguration extends AbstractTabOverlayTemplateConfiguration<DynamicSizeTabOverlayTemplate> {

    private PlayerOrderConfiguration playerOrder = PlayerOrderConfiguration.DEFAULT;

    private MarkedStringProperty playerSet;

    private ComponentConfiguration playerComponent;

    @Override
    protected DynamicSizeTabOverlayTemplate createTemplate() {
        return new DynamicSizeTabOverlayTemplate();
    }

    @Override
    protected void populateTemplate(DynamicSizeTabOverlayTemplate template, TemplateCreationContext tcc) {
        super.populateTemplate(template, tcc);

        if (ConfigValidationUtil.checkNotNull(tcc, "DYNAMIC_SIZE tab overlay", "playerSet", playerSet, null)) {
            if (!tcc.getPlayerSets().containsKey(playerSet.getValue())) {
                tcc.getErrorHandler().addError("No player set definition available for player set \"" + playerSet.getValue() + "\"", playerSet.getStartMark());
            } else {
                template.setPlayerSet(tcc.getPlayerSets().get(playerSet.getValue()));
            }
        }

        if (ConfigValidationUtil.checkNotNull(tcc, "DYNAMIC_SIZE tab overlay", "playerOrder", playerOrder, null)) {
            template.setPlayerOrder(playerOrder.toTemplate(tcc));
        }
        if (ConfigValidationUtil.checkNotNull(tcc, "DYNAMIC_SIZE tab overlay", "playerComponent", playerComponent, null)) {
            TemplateCreationContext childContext = tcc.clone();
            childContext.setDefaultIcon(new PlayerIconTemplate(PlayerPlaceholderResolver.BindPoint.PLAYER, tcc.getPlayerIconDataKey()));
            childContext.setDefaultPing(new PlayerPingTemplate(PlayerPlaceholderResolver.BindPoint.PLAYER, tcc.getPlayerPingDataKey()));
            childContext.setPlayerAvailable(true);
            template.setPlayerComponent(playerComponent.toTemplate(childContext));
        }
        template.setMorePlayersComponent(tcc.emptyComponent());
    }
}
