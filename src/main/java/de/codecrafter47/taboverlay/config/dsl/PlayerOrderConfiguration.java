package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.playerorder.PlayerOrderOptions;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerOrderConfiguration extends MarkedPropertyBase {
    public static final PlayerOrderConfiguration DEFAULT = new PlayerOrderConfiguration("alphabetically");

    private final String order;

    public PlayerOrderOptions toTemplate(TemplateCreationContext tcc) {
        // TODO
        return null;
    }
}
