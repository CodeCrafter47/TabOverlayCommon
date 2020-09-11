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

package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.PlayerOrderConfiguration;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.placeholder.OtherCountPlaceholderResolver;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.PlayersComponentTemplate;
import de.codecrafter47.taboverlay.config.template.icon.PlayerIconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PlayerPingTemplate;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class PlayersComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    private PlayerOrderConfiguration playerOrder = PlayerOrderConfiguration.DEFAULT;
    private MarkedStringProperty playerSet = null;
    private ComponentConfiguration playerComponent = new BasicComponentConfiguration("${player name}");
    @Nullable
    private ComponentConfiguration morePlayersComponent = null;
    private boolean fillSlotsVertical = false;
    private MarkedIntegerProperty minSize = new MarkedIntegerProperty(0);
    private MarkedIntegerProperty maxSize = new MarkedIntegerProperty(-1);

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {
        if (ConfigValidationUtil.checkNotNull(tcc, "!players component", "playerSet", playerSet, getStartMark())) {
            if (!tcc.getPlayerSets().containsKey(playerSet.getValue())) {
                tcc.getErrorHandler().addError("No player set definition available for player set \"" + playerSet.getValue() + "\"", playerSet.getStartMark());
            }
        }

        PlayerOrderTemplate playerOrderTemplate = PlayerOrderConfiguration.DEFAULT.toTemplate(tcc);
        if (ConfigValidationUtil.checkNotNull(tcc, "!players component", "playerOrder", playerOrder, getStartMark())) {
            playerOrderTemplate = this.playerOrder.toTemplate(tcc);
        }
        if (minSize.getValue() < 0) {
            tcc.getErrorHandler().addError("Failed to configure players component. MinSize is negative", minSize.getStartMark());
        }
        if (maxSize.getValue() != -1 && minSize.getValue() > maxSize.getValue()) {
            tcc.getErrorHandler().addError("Failed to configure players component. MaxSize is lower than minSize", maxSize.getStartMark());
        }


        TemplateCreationContext childContextP = tcc.clone();
        childContextP.setDefaultIcon(new PlayerIconTemplate(PlayerPlaceholderResolver.BindPoint.PLAYER, tcc.getPlayerIconDataKey()));
        childContextP.setDefaultPing(new PlayerPingTemplate(PlayerPlaceholderResolver.BindPoint.PLAYER, tcc.getPlayerPingDataKey()));
        childContextP.setPlayerAvailable(true);

        TemplateCreationContext childContextM = tcc.clone();
        childContextM.addPlaceholderResolver(new OtherCountPlaceholderResolver());

        ComponentTemplate playerComponentTemplate = tcc.emptyComponent(); // dummy
        if (ConfigValidationUtil.checkNotNull(tcc, "!players component", "playerComponent", playerComponent, getStartMark())) {

            playerComponentTemplate = this.playerComponent.toTemplate(childContextP);
            ComponentTemplate.LayoutInfo layoutInfo = playerComponentTemplate.getLayoutInfo();
            if (!layoutInfo.isConstantSize()) {
                tcc.getErrorHandler().addError("Failed to configure !players component. Attribute playerComponent must not have variable size.", playerComponent.getStartMark());
            }
            if (layoutInfo.isBlockAligned()) {
                tcc.getErrorHandler().addError("Failed to configure !players component. Attribute playerComponent must not require block alignment.", playerComponent.getStartMark());
            }
        }

        ComponentTemplate morePlayersComponentTemplate;
        if (this.morePlayersComponent != null) {

            morePlayersComponentTemplate = this.morePlayersComponent.toTemplate(childContextM);
            ComponentTemplate.LayoutInfo layoutInfo = morePlayersComponentTemplate.getLayoutInfo();
            if (!layoutInfo.isConstantSize()) {
                tcc.getErrorHandler().addError("Failed to configure !players component. Attribute playerComponent cannot have variable size.", morePlayersComponent.getStartMark());
            }
            if (layoutInfo.isBlockAligned()) {
                tcc.getErrorHandler().addError("Failed to configure !players component. Attribute playerComponent must not require block alignment.", morePlayersComponent.getStartMark());
            }
        } else {
            morePlayersComponentTemplate = childContextM.emptyComponent();
        }

        return PlayersComponentTemplate.builder()
                .playerOrder(playerOrderTemplate)
                .playerSet(tcc.getPlayerSets().get(playerSet.getValue()))
                .playerComponent(playerComponentTemplate)
                .morePlayersComponent(morePlayersComponentTemplate)
                .fillSlotsVertical(fillSlotsVertical)
                .minSize(minSize.getValue())
                .maxSize(maxSize.getValue())
                .columns(tcc.getColumns().orElse(1))
                .defaultIcon(tcc.getDefaultIcon())
                .defaultText(tcc.getDefaultText())
                .defaultPing(tcc.getDefaultPing())
                .build();
    }
}
