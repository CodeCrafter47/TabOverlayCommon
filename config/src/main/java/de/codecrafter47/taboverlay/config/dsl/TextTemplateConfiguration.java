/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
