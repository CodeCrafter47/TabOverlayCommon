package de.codecrafter47.taboverlay.config.icon;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;

public interface IconManager {

    IconTemplate createIconTemplate(String s, TemplateCreationContext tcc);
}
