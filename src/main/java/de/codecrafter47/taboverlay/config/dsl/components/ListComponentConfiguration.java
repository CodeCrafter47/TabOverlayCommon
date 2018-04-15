package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ListComponentTemplate;
import lombok.val;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ListComponentConfiguration extends ArrayList<ComponentConfiguration> implements ComponentConfiguration {

    private final MarkedPropertyBase delegate = new MarkedPropertyBase();

    @Override
    public Mark getStartMark() {
        return delegate.getStartMark();
    }

    @Override
    public void setStartMark(Mark startMark) {
        delegate.setStartMark(startMark);
    }

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) throws ConfigurationException {

        val componentTemplates = new ArrayList<ComponentTemplate>(size());

        for (val componentConfiguration : this) {
            if (componentConfiguration != null) {
                componentTemplates.add(componentConfiguration.toTemplate(tcc));
            } else {
                componentTemplates.add(tcc.emptySlot());
            }
        }

        return ListComponentTemplate.builder()
                .components(componentTemplates)
                .columns(tcc.getColumns().orElse(1))
                .defaultIcon(tcc.getDefaultIcon())
                .defaultText(tcc.getDefaultText())
                .defaultPing(tcc.getDefaultPing())
                .build();
    }
}
