package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.dsl.components.BasicComponentConfiguration;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.UUID;

public final class BasicComponentView extends ComponentView implements TextViewUpdateListener, PingViewUpdateListener, IconViewUpdateListener {

    private final TextView textView;
    private final PingView pingView;
    private final IconView iconView;
    private final BasicComponentConfiguration.Alignment alignment; // todo use alignment
    private UUID uuid;

    public BasicComponentView(TextView textView, PingView pingView, IconView iconView, BasicComponentConfiguration.Alignment alignment) {
        this.textView = textView;
        this.pingView = pingView;
        this.iconView = iconView;
        this.alignment = alignment;
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        Player player = getContext().getPlayer();
        uuid = player != null ? player.getUniqueID() : null;
        textView.activate(getContext(), this);
        pingView.activate(getContext(), this);
        iconView.activate(getContext(), this);
    }

    private void updateSlot() {
        Area area = getArea();
        if (area != null) {
            area.setSlot(0, uuid, iconView.getIcon(), textView.getText(), '&', pingView.getPing());
        }
    }

    @Override
    protected void onAreaUpdated() {
        updateSlot();
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        // todo shouldn't be called
    }

    @Override
    public void onTextUpdated() {
        Area area = getArea();
        if (area != null) {
            area.setText(0, textView.getText(), '&');
        }
    }

    @Override
    public void onPingUpdated() {
        Area area = getArea();
        if (area != null) {
            area.setPing(0, pingView.getPing());
        }
    }

    @Override
    public void onIconUpdated() {
        Area area = getArea();
        if (area != null) {
            area.setIcon(0, iconView.getIcon());
        }
    }

    @Override
    public int getMinSize() {
        return 1;
    }

    @Override
    public int getPreferredSize() {
        return 1;
    }

    @Override
    public int getMaxSize() {
        return 1;
    }

    @Override
    public boolean isBlockAligned() {
        return false;
    }

    @Override
    protected void onDeactivation() {
        textView.deactivate();
        pingView.deactivate();
        iconView.deactivate();
        super.onDeactivation();
    }
}
