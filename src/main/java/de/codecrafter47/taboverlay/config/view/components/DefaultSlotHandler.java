package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

class DefaultSlotHandler extends AbstractActiveElement<DefaultSlotHandler.Listener> implements ActiveElement, TextViewUpdateListener, PingViewUpdateListener, IconViewUpdateListener {

    private final TextView defaultTextView;
    private final PingView defaultPingView;
    private final IconView defaultIconView;

    DefaultSlotHandler(TextView defaultTextView, PingView defaultPingView, IconView defaultIconView) {
        this.defaultTextView = defaultTextView;
        this.defaultPingView = defaultPingView;
        this.defaultIconView = defaultIconView;
    }

    @Override
    protected void onActivation() {
        defaultTextView.activate(getContext(), this);
        defaultPingView.activate(getContext(), this);
        defaultIconView.activate(getContext(), this);
    }

    @Override
    protected void onDeactivation() {
        defaultTextView.deactivate();
        defaultPingView.deactivate();
        defaultIconView.deactivate();
    }

    String getText() {
        return defaultTextView.getText();
    }

    int getPing() {
        return defaultPingView.getPing();
    }

    Icon getIcon() {
        return defaultIconView.getIcon();
    }

    @Override
    public void onTextUpdated() {
        getListener().onDefaultSlotTextUpdated();
    }

    @Override
    public void onPingUpdated() {
        getListener().onDefaultSlotPingUpdated();
    }

    @Override
    public void onIconUpdated() {
        getListener().onDefaultSlotIconUpdated();
    }

    interface Listener {
        void onDefaultSlotTextUpdated();
        void onDefaultSlotPingUpdated();
        void onDefaultSlotIconUpdated();
    }
}
