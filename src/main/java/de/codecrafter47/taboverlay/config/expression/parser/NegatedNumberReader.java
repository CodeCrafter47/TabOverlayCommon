package de.codecrafter47.taboverlay.config.expression.parser;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.expression.token.DefaultTokens;
import de.codecrafter47.taboverlay.config.expression.token.Token;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

@RequiredArgsConstructor
public class NegatedNumberReader extends ValueReader {

    private final Token TOKEN_NEGATION;

    @Override
    public ExpressionTemplate read(TemplateCreationContext tcc, ExpressionTemplateParser parser, List<Token> tokenList, Mark mark) {
        if (tokenList.get(0) == TOKEN_NEGATION) {
            tokenList.remove(0);
            return ExpressionTemplates.negateNumber(parser.read(tcc, tokenList, mark));
        }
        return null;
    }
}
