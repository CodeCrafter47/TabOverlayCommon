package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.player.PlayerSet;
import de.codecrafter47.taboverlay.config.player.PlayerSetPartition;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartitionedPlayersView extends ListComponentView implements PlayerSetPartition.Listener {

    private final PlayerSetTemplate playerSetTemplate;
    private final ComponentTemplate playerComponentTemplate;
    private final int playerComponentSize;
    private final ComponentTemplate morePlayerComponentTemplate;
    private final int morePlayerComponentSize;
    private final PlayerOrderTemplate playerOrderTemplate;
    private final TextTemplate defaultTextTemplate;
    private final PingTemplate defaultPingTemplate;
    private final IconTemplate defaultIconTemplate;
    private final ExpressionTemplate partitionFunction;
    private PlayerSetPartition playerSetPartition;
    private final ComponentTemplate sectionHeader;
    private final ComponentTemplate sectionFooter;
    private final ComponentTemplate sectionSeparator;
    private final int minSizePerSection;
    private final int maxSizePerSection; // -1 denotes no limit
    private final SectionContextFactory sectionContextFactory;
    private final Map<String, ComponentView> sectionMap = new HashMap<>();

    public PartitionedPlayersView(int columns, PlayerSetTemplate playerSetTemplate, ComponentTemplate playerComponentTemplate, int playerComponentSize, ComponentTemplate morePlayerComponentTemplate, int morePlayerComponentSize, PlayerOrderTemplate playerOrderTemplate, TextTemplate defaultTextTemplate, PingTemplate defaultPingTemplate, IconTemplate defaultIconTemplate, ExpressionTemplate partitionFunction, ComponentTemplate sectionHeader, ComponentTemplate sectionFooter, ComponentTemplate sectionSeparator, int minSizePerSection, int maxSizePerSection, SectionContextFactory sectionContextFactory) {
        super(new ArrayList<>(), columns, defaultTextTemplate.instantiate(), defaultPingTemplate.instantiate(), defaultIconTemplate.instantiate());
        this.playerSetTemplate = playerSetTemplate;
        this.playerComponentTemplate = playerComponentTemplate;
        this.playerComponentSize = playerComponentSize;
        this.morePlayerComponentTemplate = morePlayerComponentTemplate;
        this.morePlayerComponentSize = morePlayerComponentSize;
        this.playerOrderTemplate = playerOrderTemplate;
        this.defaultTextTemplate = defaultTextTemplate;
        this.defaultPingTemplate = defaultPingTemplate;
        this.defaultIconTemplate = defaultIconTemplate;
        this.partitionFunction = partitionFunction;
        this.sectionHeader = sectionHeader;
        this.sectionFooter = sectionFooter;
        this.sectionSeparator = sectionSeparator;
        this.minSizePerSection = minSizePerSection;
        this.maxSizePerSection = maxSizePerSection;
        this.sectionContextFactory = sectionContextFactory;
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        playerSetPartition = getContext().getPlayerSetFactory().getInstance(playerSetTemplate).getPartition(partitionFunction);
        playerSetPartition.addListener(this);

        for (Map.Entry<String, ? extends PlayerSet> entry : playerSetPartition.getPartitions()) {
            addPartition(entry.getKey(), entry.getValue(), false);
        }
    }

    @Override
    protected void onDeactivation() {
        super.onDeactivation();

        playerSetPartition.removeListener(this);
    }

    @Override
    public void onPartitionAdded(String id, PlayerSet playerSet) {
        addPartition(id, playerSet, true);
    }

    private void addPartition(String id, PlayerSet playerSet, boolean notify) {
        Context sectionContext = sectionContextFactory.createSectionContext(getContext(), id, playerSet);
        ComponentView componentView = createSectionView(id, playerSet, sectionContext);
        componentView.activate(sectionContext, this);
        sectionMap.put(id, componentView);
        if (sectionSeparator != null && !super.components.isEmpty()) {
            ComponentView separator = sectionSeparator.instantiate();
            separator.activate(getContext(), this);
            super.components.add(separator);
        }
        super.components.add(componentView);
        updateLayoutRequirements(notify);
    }

    @Override
    public void onPartitionRemoved(String id) {
        ComponentView componentView = sectionMap.remove(id);
        int index = super.components.indexOf(componentView);
        if (sectionSeparator == null) {
            super.components.remove(index);
        } else if (index != 0 && super.components.size() > 1) {
            super.components.remove(index);
            ComponentView separator = super.components.remove(index - 1);
            separator.deactivate();
        } else if (index != super.components.size() - 1 && super.components.size() > 1) {
            super.components.remove(index);
            ComponentView separator = super.components.remove(index);
            separator.deactivate();
        }
        componentView.deactivate();
        updateLayoutRequirements(true);
    }

    private ComponentView createSectionView(String id, PlayerSet playerSet, Context sectionContext) {
        List<ComponentView> components = new ArrayList<>();
        if (sectionHeader != null) {
            components.add(sectionHeader.instantiate());
        }
        components.add(new PlayersComponentView(playerSet.getOrderedPlayerSet(sectionContext, playerOrderTemplate), playerComponentTemplate, playerComponentSize, morePlayerComponentTemplate, morePlayerComponentSize, defaultIconTemplate.instantiate(), defaultTextTemplate.instantiate(), defaultPingTemplate.instantiate()));
        if (sectionFooter != null) {
            components.add(sectionFooter.instantiate());
        }
        return new ContainerComponentView(new ListComponentView(components, super.columns, defaultTextTemplate.instantiate(), defaultPingTemplate.instantiate(), defaultIconTemplate.instantiate()), false, minSizePerSection, maxSizePerSection, super.columns, true);
    }

    public interface SectionContextFactory {

        Context createSectionContext(Context parent, String sectionId, PlayerSet playerSet);
    }
}
