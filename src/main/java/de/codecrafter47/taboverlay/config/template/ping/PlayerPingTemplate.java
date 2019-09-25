package de.codecrafter47.taboverlay.config.template.ping;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewPlayer;

public class PlayerPingTemplate implements PingTemplate {

    private final PlayerPlaceholderResolver.BindPoint bindPoint;
    private final DataKey<Integer> dataKey;

    public PlayerPingTemplate(PlayerPlaceholderResolver.BindPoint bindPoint, DataKey<Integer> dataKey) {
        this.bindPoint = bindPoint;
        this.dataKey = dataKey;
    }

    @Override
    public PingView instantiate() {
        return new PingViewPlayer(bindPoint, dataKey);
    }
}
