package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;

public class TextTemplateConfiguration extends MarkedPropertyBase {

    public static final TextTemplateConfiguration DEFAULT = new TextTemplateConfiguration(TemplateCreationContext::getDefaultText);

    private final TemplateConstructor templateConstructor;

    public TextTemplateConfiguration(String text) {
        templateConstructor = tcc -> TextTemplate.parse(text, getStartMark(), tcc);
    }

    private TextTemplateConfiguration(TemplateConstructor templateConstructor) {
        this.templateConstructor = templateConstructor;
    }

    public TextTemplate toTemplate(TemplateCreationContext tcc) {
        return templateConstructor.apply(tcc);
    }

    @FunctionalInterface
    private interface TemplateConstructor {

        TextTemplate apply(TemplateCreationContext tcc);
    }
}
