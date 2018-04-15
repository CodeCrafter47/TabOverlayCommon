package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerSetConfiguration extends MarkedPropertyBase {

    private MarkedStringProperty filter;

    // todo default visibility option for all player sets in root config element?
    private Visibility hiddenPlayers = Visibility.VISIBLE_TO_ADMINS;

    public PlayerSetConfiguration(String expression) {
        this.filter = new MarkedStringProperty(expression);
        this.filter.setStartMark(getStartMark()); // todo this doesn't work
    }

    public PlayerSetTemplate toTemplate(TemplateCreationContext tcc) {

        // todo handle exceptions
        TemplateCreationContext childContext = tcc.clone();
        childContext.setPlayerAvailable(true);
        ExpressionTemplate predicate = tcc.getExpressionEngine().compile(childContext, filter.getValue(), filter.getStartMark());

        return PlayerSetTemplate.builder()
                .predicate(predicate)
                .hiddenPlayersVisibility(hiddenPlayers)
                .build();
    }

    public enum Visibility {
        VISIBLE, VISIBLE_TO_ADMINS, INVISIBLE;
    }
}
