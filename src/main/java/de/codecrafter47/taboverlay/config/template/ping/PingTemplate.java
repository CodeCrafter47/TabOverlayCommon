package de.codecrafter47.taboverlay.config.template.ping;

import de.codecrafter47.taboverlay.config.view.ping.PingView;

public interface PingTemplate {

    PingTemplate ZERO = new ConstantPingTemplate(0);

    PingView instantiate();
}
