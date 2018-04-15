package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.text.TextViewAnimated;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import de.codecrafter47.taboverlay.handler.OperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlay;
import de.codecrafter47.taboverlay.util.ChildLogger;

import java.util.List;
import java.util.logging.Logger;

public class TabOverlayView<Template extends AbstractTabOverlayTemplate, Tablist extends TabOverlay & TabOverlay.HeaderAndFooter> {

    protected Tablist tablist;
    protected final Logger logger;
    protected boolean active = true;
    protected final Template tabOverlayTemplate;
    protected final Context context;
    private final HeaderFooterView<Template> headerFooterView;

    public TabOverlayView(TabView tabView, Template tabOverlayTemplate, Tablist tablist, Context context) {
        this.tabOverlayTemplate = tabOverlayTemplate;
        this.tablist = tablist;
        this.context = context;
        this.headerFooterView = new HeaderFooterView<>(tabOverlayTemplate, this.tablist);
        this.logger = new ChildLogger(tabView.getLogger(), tabOverlayTemplate.getPath().toString());

        headerFooterView.activate(context); // todo what if there is no header & footer?
    }

    public void deactivate() {
        headerFooterView.deactivate();
        active = false;
    }

    private static class HeaderFooterView<Template extends AbstractTabOverlayTemplate> implements TextViewUpdateListener {

        private final TextView header;
        private final TextView footer;
        private final TabOverlay.HeaderAndFooter tablist;

        public HeaderFooterView(Template tabOverlayTemplate, TabOverlay.HeaderAndFooter tablist) {
            this.tablist = tablist;
            if (tabOverlayTemplate.getHeader() != null) {
                header = constructTextView(tabOverlayTemplate.getHeader(), tabOverlayTemplate.getHeaderAnimationUpdateInterval());
            } else {
                header = null;
            }
            if (tabOverlayTemplate.getFooter() != null) {
                footer = constructTextView(tabOverlayTemplate.getFooter(), tabOverlayTemplate.getFooterAnimationUpdateInterval());
            } else {
                footer = null;
            }
        }

        protected void activate(Context context) {
            if (header != null) {
                header.activate(context, this);
            }
            if (footer != null) {
                footer.activate(context, this);
            }
            updateHeaderAndFooter();
        }

        protected void deactivate() {
            if (header != null) {
                header.deactivate();
            }
            if (footer != null) {
                footer.deactivate();
            }
        }

        private void updateHeaderAndFooter() {
            tablist.setHeaderFooter(header != null ? header.getText() : null, footer != null ? footer.getText() : null, '&');
        }

        private static TextView constructTextView(List<TextTemplate> templates, float interval) {
            if (templates == null || templates.isEmpty()) {
                return TextView.EMPTY;
            } else if (templates.size() == 1) {
                return templates.get(0).instantiate();
            } else {
                return new TextViewAnimated(interval, templates);
            }
        }

        @Override
        public void onTextUpdated() {
            updateHeaderAndFooter();
        }
    }
}
