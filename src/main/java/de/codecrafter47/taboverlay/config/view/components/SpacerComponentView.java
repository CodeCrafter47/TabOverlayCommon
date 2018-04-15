package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

public final class SpacerComponentView extends ComponentView implements DefaultSlotHandler.Listener {

    private final DefaultSlotHandler defaultSlotHandler;

    public SpacerComponentView(TextView textView, PingView pingView, IconView iconView) {
        this.defaultSlotHandler = new DefaultSlotHandler(textView, pingView, iconView);
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        defaultSlotHandler.activate(getContext(), this);
    }

    @Override
    protected void onAreaUpdated() {
        updateSlots();
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        throw new AssertionError("There ain't no one calling this method");
    }

    private void updateSlots() {
        Area area = getArea();
        if (area != null) {
            String text = defaultSlotHandler.getText();
            int ping = defaultSlotHandler.getPing();
            Icon icon = defaultSlotHandler.getIcon();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setSlot(i, icon, text, '&', ping);
            }
        }
    }

    @Override
    public int getMinSize() {
        return 0;
    }

    @Override
    public int getPreferredSize() {
        return 0;
    }

    @Override
    public int getMaxSize() {
        // just return a high value, higher than the maximum number of slots on the tab list,
        // but not high enough to possibly cause an integer overflow in other components doing
        // calculations with the value.
        return 360;
    }

    @Override
    public boolean isBlockAligned() {
        return false;
    }

    @Override
    protected void onDeactivation() {
        super.onDeactivation();
        defaultSlotHandler.deactivate();
    }

    @Override
    public void onDefaultSlotTextUpdated() {
        Area area = getArea();
        if (area != null) {
            String text = defaultSlotHandler.getText();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setText(i, text, '&');
            }
        }

    }

    @Override
    public void onDefaultSlotPingUpdated() {
        Area area = getArea();
        if (area != null) {
            int ping = defaultSlotHandler.getPing();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setPing(i, ping);
            }
        }
    }

    @Override
    public void onDefaultSlotIconUpdated() {
        Area area = getArea();
        if (area != null) {
            Icon icon = defaultSlotHandler.getIcon();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setIcon(i, icon);
            }
        }
    }
}
