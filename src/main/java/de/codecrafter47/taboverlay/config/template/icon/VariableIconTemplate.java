package de.codecrafter47.taboverlay.config.template.icon;

import de.codecrafter47.taboverlay.config.icon.IconManager;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.VariableIconView;

public class VariableIconTemplate implements IconTemplate {
    private final TextTemplate template;
    private final IconManager iconManager;

    public VariableIconTemplate(TextTemplate template, IconManager iconManager) {
        this.template = template;
        this.iconManager = iconManager;
    }

    @Override
    public IconView instantiate() {
        return new VariableIconView(template.instantiate(), iconManager);
    }
}
