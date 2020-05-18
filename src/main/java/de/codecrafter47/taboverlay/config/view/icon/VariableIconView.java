package de.codecrafter47.taboverlay.config.view.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.icon.IconManager;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;

public class VariableIconView extends AbstractActiveElement<IconViewUpdateListener> implements IconView {
    private static final ErrorHandler errorHandlerDummy = new ErrorHandler();
    private final TextView textView;
    private final IconManager iconManager;
    private IconView iconView;

    public VariableIconView(@NonNull TextView textView, IconManager iconManager) {
        this.textView = textView;
        this.iconManager = iconManager;
    }

    @Override
    protected void onActivation() {
        textView.activate(getContext(), this::update);

        IconTemplate iconTemplate = iconManager.createIconTemplate(textView.getText(), null, errorHandlerDummy);
        iconView = iconTemplate.instantiate();
        iconView.activate(getContext(), getListener());
    }

    private void update() {
        iconView.deactivate();

        IconTemplate iconTemplate = iconManager.createIconTemplate(textView.getText(), null, errorHandlerDummy);
        iconView = iconTemplate.instantiate();
        iconView.activate(getContext(), getListener());

        if (hasListener()) {
            getListener().onIconUpdated();
        }
    }

    @Override
    protected void onDeactivation() {
        iconView.deactivate();
        textView.deactivate();
    }

    @Override
    public Icon getIcon() {
        return iconView.getIcon();
    }
}
