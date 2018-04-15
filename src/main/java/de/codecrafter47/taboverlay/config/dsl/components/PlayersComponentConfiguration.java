package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.PlayerOrderConfiguration;
import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.exception.MarkedConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.placeholder.OtherCountPlaceholderResolver;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.playerorder.PlayerOrderOptions;
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
    private MarkedStringProperty playerSet;
    private ComponentConfiguration playerComponent; // todo add defaults
    @Nullable
    private ComponentConfiguration morePlayersComponent;
    private boolean fillSlotsVertical = false;
    private MarkedIntegerProperty minSize = new MarkedIntegerProperty(0);
    private MarkedIntegerProperty maxSize = new MarkedIntegerProperty(-1);

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) throws ConfigurationException {
        if (ConfigValidationUtil.checkNotNull(tcc, "!players component", "playerSet", playerSet, getStartMark())) {
            if (!tcc.getPlayerSets().containsKey(playerSet.getValue())) {
                tcc.getErrorHandler().addError("No player set definition available for player set \"" + playerSet.getValue() + "\"", playerSet.getStartMark());
            }
        }

        PlayerOrderOptions playerOrderOptions = null; // todo better dummy value
        if(ConfigValidationUtil.checkNotNull(tcc, "!players component", "playerOrder", playerOrder, getStartMark())) {
            playerOrderOptions = this.playerOrder.toTemplate(tcc);

        }
        if (minSize.getValue() < 0) {
            tcc.getErrorHandler().addError("Failed to configure players component. MinSize is negative", minSize.getStartMark());
        }
        if (maxSize.getValue() != -1 && minSize.getValue() > maxSize.getValue()) {
            tcc.getErrorHandler().addError("Failed to configure players component. MaxSize is lower than minSize", maxSize.getStartMark());
        }


        TemplateCreationContext childContextP = tcc.clone();
        childContextP.setDefaultIcon(new PlayerIconTemplate(PlayerPlaceholder.BindPoint.PLAYER, tcc.getPlayerIconDataKey()));
        childContextP.setDefaultPing(new PlayerPingTemplate(PlayerPlaceholder.BindPoint.PLAYER, tcc.getPlayerPingDataKey()));
        childContextP.setPlayerAvailable(true);

        TemplateCreationContext childContextM = tcc.clone();
        childContextM.addPlaceholderResolver(new OtherCountPlaceholderResolver());

        // todo check playerComponent for fixed size and not block aligned
        ComponentTemplate playerComponentTemplate = tcc.emptyComponent(); // dummy
        if (ConfigValidationUtil.checkNotNull(tcc, "!players component", "playerComponent", playerComponent, getStartMark())) {
            playerComponentTemplate = this.playerComponent.toTemplate(childContextP);
        }

        // todo check more players component for fixed size and not block aligned

        return PlayersComponentTemplate.builder()
                .playerOrder(playerOrderOptions)
                .playerSet(tcc.getPlayerSets().get(playerSet.getValue()))
                .playerComponent(playerComponentTemplate)
                .morePlayersComponent(morePlayersComponent != null ? morePlayersComponent.toTemplate(childContextM) : null)
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
