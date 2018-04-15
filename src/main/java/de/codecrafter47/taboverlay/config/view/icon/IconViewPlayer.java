package de.codecrafter47.taboverlay.config.view.icon;

import de.codecrafter47.data.api.DataHolder;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

public class IconViewPlayer extends AbstractActiveElement<IconViewUpdateListener> implements IconView, Runnable {

    private final PlayerPlaceholder.BindPoint bindPoint;
    private final DataKey<Icon> dataKey;
    private DataHolder dataHolder;

    public IconViewPlayer(PlayerPlaceholder.BindPoint bindPoint, DataKey<Icon> dataKey) {
        this.bindPoint = bindPoint;
        this.dataKey = dataKey;
    }

    @Override
    public Icon getIcon() {
        Icon icon = dataHolder.get(dataKey);
        if (icon == null) {
            icon = Icon.DEFAULT_STEVE;
        }
        return icon;
    }

    @Override
    protected void onActivation() {
        this.dataHolder = getDataHolder(getContext(), bindPoint);
        dataHolder.addDataChangeListener(dataKey, this);
    }

    @Override
    protected void onDeactivation() {
        dataHolder.removeDataChangeListener(dataKey, this);
    }

    @Override
    public void run() {
        if (hasListener()) {
            getListener().onIconUpdated();
        }
    }

    private static DataHolder getDataHolder(Context context, PlayerPlaceholder.BindPoint bindPoint) {
        DataHolder dataHolder;
        if (bindPoint == PlayerPlaceholder.BindPoint.PLAYER) {
            dataHolder = context.getPlayer();
        } else if (bindPoint == PlayerPlaceholder.BindPoint.VIEWER) {
            dataHolder = context.getViewer();
        } else {
            throw new AssertionError();
        }
        if (dataHolder == null) {
            throw new AssertionError(bindPoint.toString() + " not available");
        }
        return dataHolder;
    }
}
