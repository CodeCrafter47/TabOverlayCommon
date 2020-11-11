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

package de.codecrafter47.taboverlay.config.dsl.customplaceholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class CustomPlaceholderConfiguration extends MarkedPropertyBase {

    private MarkedIntegerProperty parameters = new MarkedIntegerProperty(0);

    public abstract PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc);

    String replaceParameters(String template, List<PlaceholderArg> args) {
        for (int i = 0; i < parameters.getValue(); i++) {
            StringBuilder replacement;
            if (i < args.size()) {
                replacement = new StringBuilder(args.get(i).getText());
                if (i == parameters.getValue() - 1) {
                    for (int j = i + 1; j < args.size(); j++) {
                        replacement.append(" ").append(args.get(j).getText());
                    }
                }
            } else {
                replacement = new StringBuilder();
            }
            template = template.replace("%" + i, replacement.toString());
        }
        return template;
    }


}
