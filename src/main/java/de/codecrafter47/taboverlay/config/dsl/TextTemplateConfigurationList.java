package de.codecrafter47.taboverlay.config.dsl;

import java.util.ArrayList;
import java.util.Collections;

public class TextTemplateConfigurationList<T> extends ArrayList<TextTemplateConfiguration> {
    private static final long serialVersionUID = 4139057608440872633L;

    public TextTemplateConfigurationList(String value) {
        super(Collections.singleton(new TextTemplateConfiguration(value)));
    }

    public TextTemplateConfigurationList() {
        super();
    }
}
