package de.codecrafter47.taboverlay.config.expression.parser;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.token.Token;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ParenthesisedExpressionReader extends ValueReader {

    private final Token OPENING_PARENTHESIS;
    private final Token CLOSING_PARENTHESIS;

    @Override
    public ExpressionTemplate read(TemplateCreationContext tcc, ExpressionTemplateParser parser, List<Token> tokenList, Mark mark) {
        if (tokenList.get(0) == OPENING_PARENTHESIS) {
            int index = 0;
            int cnt = 1;
            do {
                index += 1;
                if (tokenList.size() <= index) {
                    return null;
                }

                Token token = tokenList.get(index);
                if (token == OPENING_PARENTHESIS) {
                    cnt++;
                } else if (token == CLOSING_PARENTHESIS) {
                    cnt--;
                }
            } while (cnt != 0);

            return parser.parse(tcc, new ArrayList<>(tokenList.subList(1, index)), mark);
        }
        return null;
    }
}
