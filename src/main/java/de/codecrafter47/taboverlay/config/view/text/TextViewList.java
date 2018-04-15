package de.codecrafter47.taboverlay.config.view.text;

import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A Text view composed of multiple text views
 */
public class TextViewList extends AbstractActiveElement<TextViewUpdateListener> implements TextView {
    private final List<TextView> children;
    private final StringBuilder builder = new StringBuilder();

    public TextViewList(ArrayList<TextView> children) {
        this.children = children;
    }

    @Override
    public String getText() {
        builder.setLength(0);
        for (int i = 0; i < children.size(); i++) {
            TextView child = children.get(i);
            builder.append(child.getText());
        }
        return builder.toString();
    }

    @Override
    protected void onActivation() {
        for (int i = 0; i < children.size(); i++) {
            TextView child = children.get(i);
            child.activate(getContext(), hasListener() ? getListener() : null);
        }
    }

    @Override
    protected void onDeactivation() {
        for (int i = 0; i < children.size(); i++) {
            TextView child = children.get(i);
            child.deactivate();
        }
    }
}
