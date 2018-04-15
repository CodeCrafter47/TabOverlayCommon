package de.codecrafter47.taboverlay.config.expression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.codecrafter47.taboverlay.config.expression.operators.ListOperator;
import de.codecrafter47.taboverlay.config.expression.operators.Operator;
import de.codecrafter47.taboverlay.config.expression.parser.*;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.expression.token.*;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Builder;
import lombok.Singular;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

public class DefaultExpressionEngine implements ExpressionEngine {

    private final ExpressionTokenizer tokenizer;
    private final ExpressionTemplateParser templateParser;

    public DefaultExpressionEngine(Options options) {
        this.tokenizer = new ExpressionTokenizer(options.tokenReaders);
        this.templateParser = new ExpressionTemplateParser(options.operators, options.valueReaders);
    }

    @Override
    public ExpressionTemplate compile(TemplateCreationContext tcc, String expression, Mark mark) {
        return templateParser.parse(tcc, tokenizer.parse(tcc, expression, mark), mark);
    }

    @Builder
    public static class Options {
        @Singular
        private List<TokenReader> tokenReaders;

        @Singular
        private ImmutableMap<Token, Operator> operators;

        @Singular
        private ImmutableList<ValueReader> valueReaders;

        public static class OptionsBuilder {

            public OptionsBuilder withDefaultTokenReaders() {
                return this
                        .tokenReader(new PatternTokenReader(BooleanToken.FALSE, "false"))
                        .tokenReader(new PatternTokenReader(BooleanToken.TRUE, "true"))
                        .tokenReader(new PatternTokenReader(BooleanToken.TRUE, "all"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.AND, "and"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.AND, "&&"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.OR, "or"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.OR, "||"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.EQUAL, "=="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.EQUAL, "="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.NOT_EQUAL, "!="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.GREATER_OR_EQUAL_THAN, ">="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.LESSER_OR_EQUAL_THAN, "<="))
                        .tokenReader(new PatternTokenReader(DefaultTokens.OPENING_PARENTHESIS, "("))
                        .tokenReader(new PatternTokenReader(DefaultTokens.CLOSING_PARENTHESIS, ")"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.NEGATION, "!"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.GREATER_THAN, ">"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.LESSER_THAN, "<"))
                        .tokenReader(new PatternTokenReader(DefaultTokens.CONCAT_STRING, "."))
                        .tokenReader(new QuotedLiteralTokenReader(-10, '\"'))
                        .tokenReader(new QuotedLiteralTokenReader(-10, '\''))
                        .tokenReader(new PlaceholderTokenReader(-20))
                        .tokenReader(new NonQuotedLiteralTokenReader(-100));
            }

            public OptionsBuilder withDefaultOperators() {
                return this
                        .operator(DefaultTokens.AND, ListOperator.of(100, ExpressionTemplates::and))
                        .operator(DefaultTokens.OR, ListOperator.of(50, ExpressionTemplates::or))
                        .operator(DefaultTokens.EQUAL, Operator.of(25, ExpressionTemplates::equal))
                        .operator(DefaultTokens.NOT_EQUAL, Operator.of(25, ExpressionTemplates::notEqual))
                        .operator(DefaultTokens.GREATER_THAN, Operator.of(25, ExpressionTemplates::greater))
                        .operator(DefaultTokens.GREATER_OR_EQUAL_THAN, Operator.of(25, ExpressionTemplates::greaterOrEqual))
                        .operator(DefaultTokens.LESSER_THAN, Operator.of(25, ExpressionTemplates::less))
                        .operator(DefaultTokens.LESSER_OR_EQUAL_THAN, Operator.of(25, ExpressionTemplates::lessOrEqual))
                        .operator(DefaultTokens.CONCAT_STRING, ListOperator.of(0, ExpressionTemplates::concat));
            }

            public OptionsBuilder withDefaultValueReaders() {
                return this
                        .valueReader(new BooleanConstantReader())
                        .valueReader(new NumberConstantReader())
                        .valueReader(new StringConstantReader())
                        .valueReader(new NegatedExpressionReader(DefaultTokens.NEGATION))
                        .valueReader(new ParenthesisedExpressionReader(DefaultTokens.OPENING_PARENTHESIS, DefaultTokens.CLOSING_PARENTHESIS))
                        .valueReader(new PlaceholderReader());
            }
        }
    }
}
