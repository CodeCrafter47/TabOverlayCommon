package de.codecrafter47.taboverlay.config.template.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.view.icon.IconViewConstant;
import de.codecrafter47.taboverlay.config.view.icon.IconView;

public class ConstantIconTemplate implements IconTemplate {

    private final IconViewConstant view;

    public ConstantIconTemplate(Icon icon) {
        view = new IconViewConstant(icon);
    }
    @Override
    public IconView instantiate() {
        return view;
    }
}
