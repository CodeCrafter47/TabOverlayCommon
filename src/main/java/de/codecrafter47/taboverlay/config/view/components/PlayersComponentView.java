package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.player.OrderedPlayerSet;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.text.TextView;

import java.util.ArrayList;
import java.util.List;

public final class PlayersComponentView extends ComponentView implements OrderedPlayerSet.Listener, DefaultSlotHandler.Listener {

    private final PlayerSetTemplate playerSetTemplate;
    private final ComponentTemplate playerComponentTemplate;
    private final int playerComponentSize;
    private final ComponentTemplate morePlayerComponentTemplate;
    private final int morePlayerComponentSize;
    private final DefaultSlotHandler defaultSlotHandler;
    private final PlayerOrderTemplate playerOrderTemplate;

    private OrderedPlayerSet playerSet;
    private List<ComponentView> activePlayerComponents = new ArrayList<>();
    private ComponentView morePlayersComponent;
    private int firstDefaultSlot;

    public PlayersComponentView(PlayerSetTemplate playerSetTemplate, ComponentTemplate playerComponentTemplate, int playerComponentSize, ComponentTemplate morePlayerComponentTemplate, int morePlayerComponentSize, IconView defaultIconView, TextView defaultTextView, PingView defaultPingView, PlayerOrderTemplate playerOrderTemplate) {
        this.playerSetTemplate = playerSetTemplate;
        this.playerComponentTemplate = playerComponentTemplate;
        this.playerComponentSize = playerComponentSize;
        this.morePlayerComponentTemplate = morePlayerComponentTemplate;
        this.morePlayerComponentSize = morePlayerComponentSize;
        this.playerOrderTemplate = playerOrderTemplate;
        this.defaultSlotHandler = new DefaultSlotHandler(defaultTextView, defaultPingView, defaultIconView);
    }

    PlayersComponentView(OrderedPlayerSet playerSet, ComponentTemplate playerComponentTemplate, int playerComponentSize, ComponentTemplate morePlayerComponentTemplate, int morePlayerComponentSize, IconView defaultIconView, TextView defaultTextView, PingView defaultPingView) {
        this.playerSetTemplate = null;
        this.playerSet = playerSet;
        this.playerComponentTemplate = playerComponentTemplate;
        this.playerComponentSize = playerComponentSize;
        this.morePlayerComponentTemplate = morePlayerComponentTemplate;
        this.morePlayerComponentSize = morePlayerComponentSize;
        this.playerOrderTemplate = null;
        this.defaultSlotHandler = new DefaultSlotHandler(defaultTextView, defaultPingView, defaultIconView);
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        if (playerSetTemplate != null) {
            playerSet = getContext().getPlayerSetFactory().getInstance(playerSetTemplate).getOrderedPlayerSet(getContext(), playerOrderTemplate);
        }
        playerSet.addListener(this);

        morePlayersComponent = morePlayerComponentTemplate.instantiate();

        defaultSlotHandler.activate(getContext(), this);
    }

    @Override
    protected void onAreaUpdated() {
        Area area = getArea();
        if (area != null) {
            boolean allFit = area.getSize() >= playerSet.getCount() * playerComponentSize;
            int indexP = 0;
            int pos = 0;
            while (indexP < playerSet.getCount() && (allFit || pos + playerComponentSize + morePlayerComponentSize <= area.getSize())) {
                Area childArea = area.createChild(pos, playerComponentSize);
                if (indexP < activePlayerComponents.size()) {
                    Player player = playerSet.getPlayer(indexP);
                    if (player != activePlayerComponents.get(indexP).getContext().getPlayer()) {
                        activePlayerComponents.get(indexP).deactivate();

                        Context child = getContext().clone();
                        child.setPlayer(player);
                        ComponentView playerComponent = playerComponentTemplate.instantiate();
                        playerComponent.activate(child, this);
                        playerComponent.updateArea(childArea);
                        activePlayerComponents.set(indexP, playerComponent);
                    } else {
                        activePlayerComponents.get(indexP).updateArea(childArea);
                    }
                } else {
                    Context child = getContext().clone();
                    child.setPlayer(playerSet.getPlayer(indexP));
                    ComponentView playerComponent = playerComponentTemplate.instantiate();
                    playerComponent.activate(child, this);
                    playerComponent.updateArea(childArea);
                    activePlayerComponents.add(playerComponent);
                }
                indexP++;
                pos += playerComponentSize;
            }
            for (int j = activePlayerComponents.size() - 1; j >= indexP; j--) {
                ComponentView playerComponent = activePlayerComponents.get(j);
                playerComponent.deactivate();
                activePlayerComponents.remove(j);
            }
            if (!allFit && morePlayerComponentSize != 0) {
                if (morePlayersComponent == null) {
                    morePlayersComponent = morePlayerComponentTemplate.instantiate();
                }
                if (morePlayersComponent.isActive()) {
                    morePlayersComponent.deactivate();
                }
                Context child = getContext().clone();
                child.setCustomObject(ContextKeys.OTHER_COUNT, playerSet.getCount() - indexP);
                morePlayersComponent.activate(child, this);
                morePlayersComponent.updateArea(area.createChild(pos, morePlayerComponentSize));
                pos += morePlayerComponentSize;
            }
            int upper = area.getSize();
            firstDefaultSlot = pos;
            while (pos < upper) {
                area.setSlot(pos, defaultSlotHandler.getIcon(), defaultSlotHandler.getText(), '&', defaultSlotHandler.getPing());
                pos++;
            }
        }
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        // don't think anyone should be calling this
        throw new AssertionError("requestLayoutUpdate of PlayersComponentView should not be called");
    }

    @Override
    public void onPlayerRemoved(Player player) {
        int indexP;
        for (indexP = 0; indexP < activePlayerComponents.size(); indexP++) {
            if (player == activePlayerComponents.get(indexP).getContext().getPlayer()) {
                ComponentView playerComponent = activePlayerComponents.remove(indexP);
                playerComponent.deactivate();
            }
        }
        Area area = getArea();
        if (area != null) {
            boolean allFit = area.getSize() >= playerSet.getCount() * playerComponentSize;
            int pos = indexP * playerComponentSize;
            while (indexP < playerSet.getCount() && (allFit || pos + playerComponentSize + morePlayerComponentSize <= area.getSize())) {
                if (indexP < activePlayerComponents.size()) {
                    activePlayerComponents.get(indexP).updateArea(area.createChild(pos, playerComponentSize));
                } else {
                    Context child = getContext().clone();
                    child.setPlayer(playerSet.getPlayer(indexP));
                    ComponentView playerComponent = playerComponentTemplate.instantiate();
                    playerComponent.activate(child, this);
                    playerComponent.updateArea(area.createChild(pos, playerComponentSize));
                    activePlayerComponents.add(playerComponent);
                }
                indexP++;
                pos += playerComponentSize;
            }
            for (int j = activePlayerComponents.size() - 1; j >= indexP; j--) {
                ComponentView playerComponent = activePlayerComponents.get(j);
                playerComponent.deactivate();
                activePlayerComponents.remove(j);
            }
            if (!allFit && morePlayerComponentSize != 0) {
                if (morePlayersComponent == null) {
                    morePlayersComponent = morePlayerComponentTemplate.instantiate();
                }
                if (morePlayersComponent.isActive()) {
                    morePlayersComponent.deactivate();
                }
                Context child = getContext().clone();
                child.setCustomObject(ContextKeys.OTHER_COUNT, playerSet.getCount() - indexP);
                morePlayersComponent.activate(child, this);
                morePlayersComponent.updateArea(area.createChild(pos, morePlayerComponentSize));
                pos += morePlayerComponentSize;
            }
            int upper = Integer.min(area.getSize(), firstDefaultSlot);
            firstDefaultSlot = pos;
            while (pos < upper) {
                area.setSlot(pos, defaultSlotHandler.getIcon(), defaultSlotHandler.getText(), '&', defaultSlotHandler.getPing());
                pos++;
            }
        }
        getListener().requestLayoutUpdate(this);
    }

    @Override
    public void onUpdate(boolean newPlayers) {
        if (newPlayers) {
            // todo don't do this each time
            getListener().requestLayoutUpdate(this);
        } else {
            Area area = getArea();
            if (area != null) {
                for (int i = 0; i < activePlayerComponents.size(); i++) {
                    Player player = playerSet.getPlayer(i);
                    if (player != activePlayerComponents.get(i).getContext().getPlayer()) {
                        Area childArea = activePlayerComponents.get(i).getArea();
                        activePlayerComponents.get(i).deactivate();

                        Context child = getContext().clone();
                        child.setPlayer(player);
                        ComponentView playerComponent = playerComponentTemplate.instantiate();
                        playerComponent.activate(child, this);
                        playerComponent.updateArea(childArea);
                        activePlayerComponents.set(i, playerComponent);
                    }
                }
            }
        }
    }

    @Override
    public int getMinSize() {
        return 0;
    }

    @Override
    public int getPreferredSize() {
        return playerSet.getCount();
    }

    @Override
    public int getMaxSize() {
        return playerSet.getCount();
    }

    @Override
    public boolean isBlockAligned() {
        return false;
    }

    @Override
    protected void onDeactivation() {
        super.onDeactivation();

        for (ComponentView playerComponent : activePlayerComponents) {
            playerComponent.deactivate();
        }
        activePlayerComponents.clear();

        if (morePlayersComponent.isActive()) {
            morePlayersComponent.deactivate();
        }
        morePlayersComponent = null;

        playerSet.removeListener(this);
        playerSet = null;

        defaultSlotHandler.deactivate();
    }

    @Override
    public void onDefaultSlotTextUpdated() {
        Area area = getArea();
        if (area != null) {
            int pos = firstDefaultSlot;
            int upper = area.getSize();
            while (pos < upper) {
                area.setText(pos, defaultSlotHandler.getText(), '&');
                pos++;
            }
        }
    }

    @Override
    public void onDefaultSlotPingUpdated() {
        Area area = getArea();
        if (area != null) {
            int pos = firstDefaultSlot;
            int upper = area.getSize();
            while (pos < upper) {
                area.setPing(pos, defaultSlotHandler.getPing());
                pos++;
            }
        }
    }

    @Override
    public void onDefaultSlotIconUpdated() {
        Area area = getArea();
        if (area != null) {
            int pos = firstDefaultSlot;
            int upper = area.getSize();
            while (pos < upper) {
                area.setIcon(pos, defaultSlotHandler.getIcon());
                pos++;
            }
        }
    }
}
