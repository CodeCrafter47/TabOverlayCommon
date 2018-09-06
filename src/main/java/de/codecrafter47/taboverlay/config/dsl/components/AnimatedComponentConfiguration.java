package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.exception.MarkedConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedFloatProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedListProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.AnimatedComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnimatedComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    private MarkedListProperty<ComponentConfiguration> components;
    private MarkedFloatProperty interval;

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) throws ConfigurationException {

        if ((ConfigValidationUtil.checkNotNull(tcc, "!animated component", "components", components, getStartMark())
                && ConfigValidationUtil.checkNotEmpty(tcc, "!animated component", "components", components, components.getStartMark()))
                & ConfigValidationUtil.checkNotNull(tcc, "!animated component", "interval", interval, getStartMark())
                && ConfigValidationUtil.checkRange(tcc, "!animated component", "interval", interval.getValue(), 0.05f, 9999f, interval.getStartMark())) {

            List<ComponentTemplate> componentTemplates = new ArrayList<>(components.size());
            for (ComponentConfiguration component : components) {
                if (component == null) {
                    componentTemplates.add(tcc.emptySlot());
                } else {
                    componentTemplates.add(component.toTemplate(tcc));
                }
            }

            // check child templates for constant size
            if (!componentTemplates.get(0).getLayoutInfo().isConstantSize()) {
                throw new MarkedConfigurationException("Animated components can only contain components of constant size.", components.get(0).getStartMark());
            }
            int size = componentTemplates.get(0).getLayoutInfo().getMinSize();

            for (int i = 1; i < componentTemplates.size(); i++) {
                if (!componentTemplates.get(i).getLayoutInfo().isConstantSize()) {
                    throw new MarkedConfigurationException("Animated components can only contain components of constant size.", (components.get(i) != null ? components.get(i) : components).getStartMark());
                }
                if (componentTemplates.get(i).getLayoutInfo().getMinSize() != size) {
                    throw new MarkedConfigurationException("Animated components can only contain components of the same size.", (components.get(i) != null ? components.get(i) : components).getStartMark());
                }
            }

            return AnimatedComponentTemplate.builder()
                    .components(componentTemplates)
                    .interval(interval.getValue())
                    .build();
        } else {
            return tcc.emptyComponent(); // dummy component
        }
    }
}
