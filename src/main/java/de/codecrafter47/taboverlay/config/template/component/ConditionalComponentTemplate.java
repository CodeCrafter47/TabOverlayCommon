package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.ConditionalComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ConditionalComponentTemplate implements ComponentTemplate {
    private ExpressionTemplate condition;
    private ComponentTemplate trueReplacement;
    private ComponentTemplate falseReplacement;

    @Override
    public LayoutInfo getLayoutInfo() {
        LayoutInfo layout1 = trueReplacement.getLayoutInfo();
        LayoutInfo layout2 = falseReplacement.getLayoutInfo();
        return LayoutInfo.builder()
                .constantSize(layout1.isConstantSize() && layout2.isConstantSize() && layout1.getMinSize() == layout2.getMinSize())
                .minSize(Integer.max(layout1.getMinSize(), layout2.getMinSize()))
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new ConditionalComponentView(condition.instantiateWithBooleanResult(), trueReplacement, falseReplacement);
    }
}
