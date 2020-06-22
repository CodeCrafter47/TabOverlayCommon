package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.handler.HeaderAndFooterOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;

public class TabOverlayView {

    private final AbstractActiveElement<?> contentView;
    private final AbstractActiveElement<?> headerFooterView;

    public static TabOverlayView create(TabView tabView, TabOverlayHandler handler, Context context, AbstractTabOverlayTemplate template) {
        AbstractActiveElement<?> headerFooterView;
        if (template.showHeaderAndFooter()) {
            headerFooterView = new HeaderAndFooterView(template, handler.enterHeaderAndFooterOperationMode(HeaderAndFooterOperationMode.CUSTOM));
        } else {
            handler.enterHeaderAndFooterOperationMode(HeaderAndFooterOperationMode.PASS_TROUGH);
            headerFooterView = null;
        }
        AbstractActiveElement<?> contentView = template.createContentView(tabView, handler);

        return new TabOverlayView(context, contentView, headerFooterView);
    }

    private TabOverlayView(Context context, AbstractActiveElement<?> contentView, AbstractActiveElement<?> headerFooterView) {
        this.contentView = contentView;
        this.headerFooterView = headerFooterView;

        this.contentView.activate(context, null);
        if (headerFooterView != null) {
            this.headerFooterView.activate(context, null);
        }

    }

    public void deactivate() {
        contentView.deactivate();
        if (headerFooterView != null) {
            headerFooterView.deactivate();
        }
    }
}
