package de.codecrafter47.taboverlay.config.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import org.yaml.snakeyaml.error.Mark;

import java.util.function.Consumer;

public interface IconManager {

    IconTemplate createIconTemplate(String s, Mark mark, ErrorHandler errorHandler);

    void createIcon(String s, Consumer<Icon> listener) throws Exception;
}
