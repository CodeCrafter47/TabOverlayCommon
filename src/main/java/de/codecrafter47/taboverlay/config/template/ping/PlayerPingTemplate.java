package de.codecrafter47.taboverlay.config.template.ping;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewPlayer;

public class PlayerPingTemplate implements PingTemplate {

    private final PlayerPlaceholder.BindPoint bindPoint;
    private final DataKey<Integer> dataKey;

    public PlayerPingTemplate(PlayerPlaceholder.BindPoint bindPoint, DataKey<Integer> dataKey) {
        this.bindPoint = bindPoint;
        this.dataKey = dataKey;
    }

    @Override
    public PingView instantiate() {
        return new PingViewPlayer(bindPoint, dataKey);
    }
}
