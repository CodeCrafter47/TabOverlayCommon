package de.codecrafter47.taboverlay.config.expression.parser;

import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.token.PlaceholderToken;
import de.codecrafter47.taboverlay.config.expression.token.Token;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderException;
import de.codecrafter47.taboverlay.config.placeholder.UnknownPlaceholderException;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

public class PlaceholderReader extends ValueReader {
    @Override
    public ExpressionTemplate read(TemplateCreationContext tcc, ExpressionTemplateParser parser, List<Token> tokenList, Mark mark) {
        if (tokenList.get(0) instanceof PlaceholderToken) {
            PlaceholderToken token = (PlaceholderToken) tokenList.remove(0);
            try {
                tcc.getErrorHandler().enterContext("in use of placeholder ${" + token.getValue() + "}", mark);
                try {
                    return tcc.getPlaceholderResolverChain().resolve(token.getValue().split(" "), tcc);
                } finally {
                    tcc.getErrorHandler().leaveContext();
                }
            } catch (UnknownPlaceholderException e) {
                tcc.getErrorHandler().addWarning("Unknown placeholder ${" + token.getValue() + "}", mark);
            } catch (PlaceholderException e) {
                String message = "Error in placeholder ${\"" + token.getValue() + "}:\n" + e.getMessage();
                if (e.getCause() != null) {
                    message = message + "\nCaused by: " + e.getCause().getMessage();
                }
                tcc.getErrorHandler().addWarning(message, mark);
            }
            return ConstantExpressionTemplate.of(""); // dummy
        }
        return null;
    }
}
