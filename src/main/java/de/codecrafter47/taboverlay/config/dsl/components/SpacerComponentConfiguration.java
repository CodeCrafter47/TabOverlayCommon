package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.SpacerComponentTemplate;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpacerComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    public SpacerComponentConfiguration(String ignored) {
        // todo check whether this fixes using just !spacer
    }

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {
        return SpacerComponentTemplate.builder()
                .defaultIcon(tcc.getDefaultIcon())
                .defaultText(tcc.getDefaultText())
                .defaultPing(tcc.getDefaultPing())
                .build();
    }
}
