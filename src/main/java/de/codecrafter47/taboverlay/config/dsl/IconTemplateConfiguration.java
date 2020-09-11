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
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.icon.PlayerIconTemplate;
import de.codecrafter47.taboverlay.config.template.icon.VariableIconTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import org.yaml.snakeyaml.error.Mark;

public class IconTemplateConfiguration extends MarkedPropertyBase {

    public static final IconTemplateConfiguration DEFAULT = new IconTemplateConfiguration(TemplateCreationContext::getDefaultIcon);

    private final TemplateConstructor templateConstructor;

    private IconTemplateConfiguration(TemplateConstructor templateConstructor) {
        this.templateConstructor = templateConstructor;
    }

    public IconTemplateConfiguration(String value) {
        this.templateConstructor = tcc -> parse(value, getStartMark(), tcc);
    }

    private IconTemplate parse(String value, Mark mark, TemplateCreationContext tcc) {
        if (value.equals("${player skin}")) {
            if (!tcc.isPlayerAvailable()) {
                tcc.getErrorHandler().addWarning("${player skin} cannot be used here", getStartMark());
                return tcc.getDefaultIcon();
            }
            return new PlayerIconTemplate(PlayerPlaceholderResolver.BindPoint.PLAYER, tcc.getPlayerIconDataKey());
        } else if (value.equals("${viewer skin}")) {
            if (!tcc.isViewerAvailable()) {
                tcc.getErrorHandler().addWarning("${viewer skin} cannot be used here", getStartMark());
                return tcc.getDefaultIcon();
            }
            return new PlayerIconTemplate(PlayerPlaceholderResolver.BindPoint.VIEWER, tcc.getPlayerIconDataKey());
        } else {
            if (value.contains("${")) {
                TextTemplate template = TextTemplate.parse(value, mark, tcc);
                return new VariableIconTemplate(template, tcc.getIconManager());
            } else {
                return tcc.getIconManager().createIconTemplate(value, mark, tcc.getErrorHandler());
            }
        }
    }

    public IconTemplate toTemplate(TemplateCreationContext tcc) {
        return templateConstructor.apply(tcc);
    }

    @FunctionalInterface
    private interface TemplateConstructor {

        IconTemplate apply(TemplateCreationContext tcc);
    }
}
