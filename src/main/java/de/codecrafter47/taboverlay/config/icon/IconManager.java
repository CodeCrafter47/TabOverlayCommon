package de.codecrafter47.taboverlay.config.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import org.yaml.snakeyaml.error.Mark;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public interface IconManager {

    IconTemplate createIconTemplate(String s, Mark mark, ErrorHandler errorHandler);

    CompletableFuture<Icon> createIcon(BufferedImage image);

    CompletableFuture<Icon> createIconFromName(String name);
}
