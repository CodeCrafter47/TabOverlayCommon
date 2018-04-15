package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.context.ContextKey;
import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerSet;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.text.TextView;

import java.util.ArrayList;
import java.util.List;

public final class PlayersComponentView extends ComponentView implements PlayerSet.Listener, DefaultSlotHandler.Listener {

    private final PlayerSetTemplate playerSetTemplate;
    private final ComponentTemplate playerComponentTemplate;
    private final int playerComponentSize;
    private final ComponentTemplate morePlayerComponentTemplate;
    private final int morePlayerComponentSize;
    private final DefaultSlotHandler defaultSlotHandler;

    private PlayerSet playerSet;
    private List<Player> players = null;
    private List<ComponentView> activePlayerComponents = new ArrayList<>();
    private ComponentView morePlayersComponent;
    private int firstDefaultSlot;
    // todo sort the players

    public PlayersComponentView(PlayerSetTemplate playerSetTemplate, ComponentTemplate playerComponentTemplate, int playerComponentSize, ComponentTemplate morePlayerComponentTemplate, int morePlayerComponentSize, IconView defaultIconView, TextView defaultTextView, PingView defaultPingView) {
        this.playerSetTemplate = playerSetTemplate;
        this.playerComponentTemplate = playerComponentTemplate;
        this.playerComponentSize = playerComponentSize;
        this.morePlayerComponentTemplate = morePlayerComponentTemplate;
        this.morePlayerComponentSize = morePlayerComponentSize;
        this.defaultSlotHandler = new DefaultSlotHandler(defaultTextView, defaultPingView, defaultIconView);
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        playerSet = getContext().getPlayerSetFactory().getInstance(playerSetTemplate);
        playerSet.addListener(this);

        players = new ArrayList<>(playerSet.getPlayers());
        morePlayersComponent = morePlayerComponentTemplate.instantiate();

        defaultSlotHandler.activate(getContext(), this);
    }

    @Override
    protected void onAreaUpdated() {
        Area area = getArea();
        if (area != null) {
            boolean allFit = area.getSize() >= players.size() * playerComponentSize;
            int indexP = 0;
            int pos = 0;
            while (indexP < players.size() && (allFit || pos + playerComponentSize + morePlayerComponentSize <= area.getSize())) {
                if (indexP < activePlayerComponents.size()) {
                    activePlayerComponents.get(indexP).updateArea(area.createChild(pos, playerComponentSize));
                } else {
                    Context child = getContext().clone();
                    child.setPlayer(players.get(indexP));
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
            if (!allFit) {
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
    public void onPlayerAdded(Player player) {
        players.add(player);
        /*
        Area area = getArea();
        if (area != null) {
            boolean allFit = area.getSize() >= players.size() * playerComponentSize;
            int indexP = activePlayerComponents.size();
            int pos = indexP * playerComponentSize;
            while (indexP < players.size() && (allFit || pos + playerComponentSize + morePlayerComponentSize <= area.getSize())) {
                if (indexP < activePlayerComponents.size()) {
                    activePlayerComponents.get(indexP).updateArea(area.createChild(pos, playerComponentSize));
                } else {
                    Context child = getContext().clone();
                    child.setPlayer(players.get(indexP));
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
            if (!allFit) {
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
        }*/
        // todo
        getListener().requestLayoutUpdate(this);
    }

    @Override
    public void onPlayerRemoved(Player player) {
        int indexP = players.indexOf(player);
        if (indexP < 0) {
            // shouldn't happen
            throw new AssertionError("Removed non-existent player " + player);
        }
        players.remove(indexP);
        if (indexP < activePlayerComponents.size()) {
            ComponentView playerComponent = activePlayerComponents.remove(indexP);
            playerComponent.deactivate();
        } else {
            indexP = activePlayerComponents.size();
        }
        Area area = getArea();
        if (area != null) {
            boolean allFit = area.getSize() >= players.size() * playerComponentSize;
            int pos = indexP * playerComponentSize;
            while (indexP < players.size() && (allFit || pos + playerComponentSize + morePlayerComponentSize <= area.getSize())) {
                if (indexP < activePlayerComponents.size()) {
                    activePlayerComponents.get(indexP).updateArea(area.createChild(pos, playerComponentSize));
                } else {
                    Context child = getContext().clone();
                    child.setPlayer(players.get(indexP));
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
            if (!allFit) {
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
