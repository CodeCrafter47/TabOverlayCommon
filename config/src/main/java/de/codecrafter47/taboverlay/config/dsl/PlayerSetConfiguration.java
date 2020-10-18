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
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.yaml.snakeyaml.error.Mark;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class PlayerSetConfiguration extends MarkedPropertyBase {

    private MarkedStringProperty filter;

    private Visibility hiddenPlayers = null;

    private transient boolean fixMark;

    public PlayerSetConfiguration(String expression) {
        this.filter = new MarkedStringProperty(expression);
        this.fixMark = true;
    }

    @Override
    public void setStartMark(Mark startMark) {
        super.setStartMark(startMark);
        if (fixMark) {
            filter.setStartMark(startMark);
        }
    }

    public PlayerSetTemplate toTemplate(TemplateCreationContext tcc) {

        TemplateCreationContext childContext = tcc.clone();
        childContext.setPlayerAvailable(true);
        ExpressionTemplate predicate = tcc.getExpressionEngine().compile(childContext, filter.getValue(), filter.getStartMark());

        return PlayerSetTemplate.builder()
                .predicate(predicate)
                .hiddenPlayersVisibility(Optional.ofNullable(hiddenPlayers).orElse(tcc.getDefaultHiddenPlayerVisibility()))
                .build();
    }

    public enum Visibility {
        VISIBLE, VISIBLE_TO_ADMINS, INVISIBLE;
    }
}
