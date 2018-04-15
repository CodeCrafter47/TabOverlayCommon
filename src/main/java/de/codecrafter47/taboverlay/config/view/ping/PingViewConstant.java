package de.codecrafter47.taboverlay.config.view.ping;

import de.codecrafter47.taboverlay.config.context.Context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PingViewConstant implements PingView {
    private final int ping;

    public PingViewConstant(int ping) {
        this.ping = ping;
    }

    @Override
    public int getPing() {
        return ping;
    }

    @Override
    public void activate(@Nonnull Context context, @Nullable PingViewUpdateListener listener) {
        // nothing to do here
    }

    @Override
    public void deactivate() {
        // nothing to do here
    }
}
