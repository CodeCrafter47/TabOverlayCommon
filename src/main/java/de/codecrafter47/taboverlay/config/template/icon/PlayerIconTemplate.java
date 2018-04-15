package de.codecrafter47.taboverlay.config.template.icon;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewPlayer;

public class PlayerIconTemplate implements IconTemplate {

    private final PlayerPlaceholder.BindPoint bindPoint;
    private final DataKey<Icon> dataKey;

    public PlayerIconTemplate(PlayerPlaceholder.BindPoint bindPoint, DataKey<Icon> dataKey) {
        this.bindPoint = bindPoint;
        this.dataKey = dataKey;
    }

    @Override
    public IconView instantiate() {
        return new IconViewPlayer(bindPoint, dataKey);
    }
}
