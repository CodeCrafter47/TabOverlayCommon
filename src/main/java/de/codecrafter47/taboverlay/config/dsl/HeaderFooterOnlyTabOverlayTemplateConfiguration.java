package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.template.HeaderFooterOnlyTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderFooterOnlyTabOverlayTemplateConfiguration extends AbstractTabOverlayTemplateConfiguration<HeaderFooterOnlyTabOverlayTemplate> {

    @Override
    protected HeaderFooterOnlyTabOverlayTemplate createTemplate() {
        return new HeaderFooterOnlyTabOverlayTemplate();
    }

    @Override
    protected void populateTemplate(HeaderFooterOnlyTabOverlayTemplate template, TemplateCreationContext tcc) {
        super.populateTemplate(template, tcc);
    }
}
