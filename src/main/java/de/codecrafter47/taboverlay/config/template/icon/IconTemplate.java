package de.codecrafter47.taboverlay.config.template.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.view.icon.IconView;

public interface IconTemplate {
    IconTemplate STEVE = new ConstantIconTemplate(Icon.DEFAULT_STEVE);

    IconView instantiate();
}
