package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

public class CustomPlaceholderAlias extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private TextView replacement;

    public CustomPlaceholderAlias(TextTemplate replacement) {
        this.replacement = replacement.instantiate();
    }

    @Override
    public String getData() {
        return replacement.getText();
    }

    @Override
    protected void onActivation() {
        replacement.activate(getContext(), this);
    }

    @Override
    protected void onDeactivation() {
        replacement.deactivate();
    }

    @Override
    public void onTextUpdated() {
        if (hasListener()) {
            getListener().run();
        }
    }
}
