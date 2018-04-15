package de.codecrafter47.taboverlay.config.template.ping;

import de.codecrafter47.taboverlay.config.view.ping.PingViewConstant;
import de.codecrafter47.taboverlay.config.view.ping.PingView;

public class ConstantPingTemplate implements PingTemplate {

    private final PingViewConstant view;

    public ConstantPingTemplate(int ping) {
        view = new PingViewConstant(ping);
    }
    @Override
    public PingView instantiate() {
        return view;
    }
}
