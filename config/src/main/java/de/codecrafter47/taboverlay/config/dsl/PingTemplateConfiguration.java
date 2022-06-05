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
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.ping.ConstantPingTemplate;
import de.codecrafter47.taboverlay.config.template.ping.ExpressionPingTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PlayerPingTemplate;

public class PingTemplateConfiguration extends MarkedPropertyBase {

    public static final PingTemplateConfiguration DEFAULT = new PingTemplateConfiguration(TemplateCreationContext::getDefaultPing);
    public static final PingTemplateConfiguration ZERO = new PingTemplateConfiguration(tcc -> PingTemplate.ZERO);

    private final TemplateConstructor templateConstructor;

    private PingTemplateConfiguration(TemplateConstructor templateConstructor) {
        this.templateConstructor = templateConstructor;
    }

    public PingTemplateConfiguration(String value) {
        this.templateConstructor = tcc -> create(value, tcc);
    }

    private PingTemplate create(String value, TemplateCreationContext tcc) {
        if (value.equals("${player ping}")) {
            if (!tcc.isPlayerAvailable()) {
                tcc.getErrorHandler().addWarning("${player ping} cannot be used here", getStartMark());
                return tcc.getDefaultPing();
            }
            return new PlayerPingTemplate(PlayerPlaceholderResolver.BindPoint.PLAYER, tcc.getPlayerPingDataKey());
        } else if (value.equals("${viewer ping}")) {
            if (!tcc.isViewerAvailable()) {
                tcc.getErrorHandler().addWarning("${viewer ping} cannot be used here", getStartMark());
                return tcc.getDefaultPing();
            }
            return new PlayerPingTemplate(PlayerPlaceholderResolver.BindPoint.VIEWER, tcc.getPlayerPingDataKey());
        } else {
            int ping;
            try {
                ping = Integer.parseInt(value);
                return new ConstantPingTemplate(ping);
            } catch (NumberFormatException e) {
                // try parse ping as an expression
                ExpressionTemplate expression = tcc.getExpressionEngine().compile(tcc, value, getStartMark());
                return new ExpressionPingTemplate(expression);
            }
        }
    }

    public PingTemplate toTemplate(TemplateCreationContext tcc) {
        return templateConstructor.apply(tcc);
    }

    @FunctionalInterface
    private interface TemplateConstructor {

        PingTemplate apply(TemplateCreationContext tcc);
    }
}
