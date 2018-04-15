package de.codecrafter47.taboverlay.config.expression;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

// TODO test expressions such as "4.2"
// TODO allow OR instead of or
public interface ExpressionEngine {

    ExpressionTemplate compile(TemplateCreationContext tcc, String expression, Mark mark);
}
