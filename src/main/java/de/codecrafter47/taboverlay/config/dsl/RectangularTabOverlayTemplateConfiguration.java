package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.template.RectangularTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RectangularTabOverlayTemplateConfiguration extends AbstractTabOverlayTemplateConfiguration<RectangularTabOverlayTemplate> {

    private MarkedIntegerProperty size = null;
    private MarkedIntegerProperty columns = null;

    private IconTemplateConfiguration defaultIcon;

    private PingTemplateConfiguration defaultPing = PingTemplateConfiguration.ZERO;

    private ComponentConfiguration components;

    @Override
    protected RectangularTabOverlayTemplate createTemplate() {
        return new RectangularTabOverlayTemplate();
    }

    @Override
    protected void populateTemplate(RectangularTabOverlayTemplate template, TemplateCreationContext tcc) throws ConfigurationException {
        super.populateTemplate(template, tcc);

        TemplateCreationContext child = tcc.clone();

        if (size == null && columns == null) {
            tcc.getErrorHandler().addError("Failed to configure RECTANGULAR tab list. Either the size or the columns property must be set.", null);
        } else if (size != null && columns != null) {
            tcc.getErrorHandler().addError("Failed to configure RECTANGULAR tab list. The size and columns options are mutually exclusive. Only set one of them.", null);
        } else if (size != null && size.getValue() < 0) {
            tcc.getErrorHandler().addError("Failed to configure RECTANGULAR tab list. Size is negative.", size.getStartMark());
        } else if (size != null && !ConfigValidationUtil.isRectangular(size.getValue())) {
            tcc.getErrorHandler().addError("Failed to configure RECTANGULAR tab list. size is not rectangular.", size.getStartMark());
        } else if (columns != null && columns.getValue() < 1) {
            tcc.getErrorHandler().addError("Failed to configure RECTANGULAR tab list. Columns is not positive.", size.getStartMark());
        } else {
            child.setColumns(columns != null ? columns.getValue() : (size.getValue() + 19) / 20);
            template.setSize(size != null ? size.getValue() : -1);
            template.setColumns(columns != null ? columns.getValue() : -1);
        }

        if (size != null && size.getValue() > 80) {
            tcc.getErrorHandler().addWarning("size must not be greater than 80.", size.getStartMark());
        }

        if (columns != null && columns.getValue() > 4) {
            tcc.getErrorHandler().addWarning("columns must not be greater than 4.", columns.getStartMark());
        }

        if (ConfigValidationUtil.checkNotNull(tcc, "RECTANGULAR tab overlay", "defaultIcon", defaultIcon, null)) {
            child.setDefaultIcon(defaultIcon.toTemplate(tcc));
        }
        if (ConfigValidationUtil.checkNotNull(tcc, "RECTANGULAR tab overlay", "defaultPing", defaultPing, null)) {
            child.setDefaultPing(defaultPing.toTemplate(tcc));
        }
        if (ConfigValidationUtil.checkNotNull(tcc, "RECTANGULAR tab overlay", "components", components, null)) {
            ComponentTemplate contentRoot = components.toTemplate(child);
            template.setContentRoot(contentRoot);
            int contentMinSize = contentRoot.getLayoutInfo().getMinSize();
            if (size != null && size.getValue() < contentMinSize) {
                tcc.getErrorHandler().addWarning("size set to " + size.getValue() + ", but to display everything at least " + contentMinSize + " slots would be required.", size.getStartMark());
            }
            if (columns != null && columns.getValue() * 20 < contentMinSize) {
                tcc.getErrorHandler().addWarning("columns set to " + columns.getValue() + " which implies a maximum size of " + (columns.getValue() * 20) + ", but to display everything at least " + contentMinSize + " slots would be required.", columns.getStartMark());
            }
        }
    }
}
