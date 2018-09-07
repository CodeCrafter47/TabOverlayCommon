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
