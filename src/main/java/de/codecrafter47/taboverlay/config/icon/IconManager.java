package de.codecrafter47.taboverlay.config.icon;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import org.yaml.snakeyaml.error.Mark;

public interface IconManager {

    IconTemplate createIconTemplate(String s, Mark mark, TemplateCreationContext tcc);
}
