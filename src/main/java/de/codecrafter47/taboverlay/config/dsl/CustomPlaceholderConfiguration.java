package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.*;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.placeholder.*;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static class Conditional extends CustomPlaceholderConfiguration {
        @Getter
        @Setter
        private MarkedStringProperty condition;
        private MarkedStringProperty trueReplacement;
        private MarkedStringProperty falseReplacement;

        // alias trueReplacement as true
        public MarkedStringProperty getTrue() {
            return trueReplacement;
        }

        public void setTrue(MarkedStringProperty trueReplacement) {
            this.trueReplacement = trueReplacement;
        }

        // alias falseReplacement as false
        public MarkedStringProperty getFalse() {
            return falseReplacement;
        }

        public void setFalse(MarkedStringProperty falseReplacement) {
            this.falseReplacement = falseReplacement;
        }

        @Override
        public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
            ExpressionTemplate compiledCondition = ConstantExpressionTemplate.of(true); // dummy value, to continue processing in case of config errors, to find more errors
            if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !conditional", "condition", condition, getStartMark())) {
                try {
                    compiledCondition = tcc.getExpressionEngine().compile(tcc, replaceParameters(condition.getValue(), args), condition.getStartMark());
                } catch (Exception e) {
                    tcc.getErrorHandler().addError("Failed to compile condition for custom placeholder. " + e.getMessage(), condition.getStartMark());
                }
            }
            if (trueReplacement == null && falseReplacement == null) {
                tcc.getErrorHandler().addWarning("No replacements configured for conditional custom placeholder.", getStartMark());
            }
            TextTemplate trueReplacement = TextTemplate.EMPTY;
            try {
                trueReplacement = this.trueReplacement != null ? TextTemplate.parse(replaceParameters(this.trueReplacement.getValue(), args), this.trueReplacement.getStartMark(), tcc) : TextTemplate.EMPTY;
            } catch (Exception e) {
                tcc.getErrorHandler().addError("Failed to parse true replacement for custom placeholder: " + e.getMessage(), this.trueReplacement.getStartMark());
            }
            TextTemplate falseReplacement = TextTemplate.EMPTY;
            try {
                falseReplacement = this.falseReplacement != null ? TextTemplate.parse(replaceParameters(this.falseReplacement.getValue(), args), this.falseReplacement.getStartMark(), tcc) : TextTemplate.EMPTY;
            } catch (Exception e) {
                tcc.getErrorHandler().addError("Failed to parse false replacement for custom placeholder: " + e.getMessage(), this.falseReplacement.getStartMark());
            }
            ExpressionTemplate finalCompiledCondition = compiledCondition;
            TextTemplate finalTrueReplacement = trueReplacement;
            TextTemplate finalFalseReplacement = falseReplacement;
            return builder.acquireData(() -> new CustomPlaceholderConditional(finalCompiledCondition,
                    finalTrueReplacement,
                    finalFalseReplacement), TypeToken.STRING, finalCompiledCondition.requiresViewerContext() || finalTrueReplacement.requiresViewerContext() || finalFalseReplacement.requiresViewerContext());
        }
    }

    @Getter
    @Setter
    public static class Switch extends CustomPlaceholderConfiguration {
        private MarkedStringProperty expression;
        private Map<MarkedStringProperty, MarkedStringProperty> replacements;
        private MarkedStringProperty defaultReplacement;

        // alias defaultReplacement as default
        public MarkedStringProperty getDefault() {
            return defaultReplacement;
        }

        public void setDefault(MarkedStringProperty defaultReplacement) {
            this.defaultReplacement = defaultReplacement;
        }

        @Override
        public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
            ExpressionTemplate compiledExpression = ConstantExpressionTemplate.of(true); // dummy value, to continue processing in case of config errors, to find more errors
            if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !switch", "expression", expression, getStartMark())) {
                try {
                    compiledExpression = tcc.getExpressionEngine().compile(tcc, replaceParameters(expression.getValue(), args), expression.getStartMark());
                } catch (Exception e) {
                    tcc.getErrorHandler().addError("Failed to compile expression for custom placeholder: " + e.getMessage(), expression.getStartMark());
                }
            }

            Map<String, TextTemplate> replacementMap = new HashMap<>(replacements.size());
            if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !switch", "replacements", replacements, getStartMark())) {
                for (val entry : replacements.entrySet()) {
                    if (entry.getKey() == null) {
                        tcc.getErrorHandler().addWarning("Replacement with missing or null key in !switch custom placeholder", entry.getValue() != null ? entry.getValue().getStartMark() : getStartMark());
                    } else if (entry.getKey().getValue() == null) {
                        tcc.getErrorHandler().addWarning("Replacement with missing or null key in !switch custom placeholder", entry.getKey().getStartMark());
                    } else {
                        TextTemplate replacement = TextTemplate.EMPTY;
                        if (entry.getValue() != null) {
                            try {
                                replacement = TextTemplate.parse(replaceParameters(entry.getValue().getValue(), args), entry.getValue().getStartMark(), tcc);
                            } catch (Exception e) {
                                tcc.getErrorHandler().addError("Failed to parse replacement for custom placeholder: " + e.getMessage(), entry.getValue().getStartMark());
                            }
                        }
                        replacementMap.put(entry.getKey().getValue(), replacement);
                    }
                }
            }
            TextTemplate defaultReplacement = TextTemplate.EMPTY;
            if (this.defaultReplacement != null) {
                defaultReplacement = TextTemplate.parse(replaceParameters(this.defaultReplacement.getValue(), args), this.defaultReplacement.getStartMark(), tcc);
            }
            ExpressionTemplate finalCompiledExpression = compiledExpression;
            TextTemplate finalDefaultReplacement = defaultReplacement;
            return builder.acquireData(() -> new CustomPlaceholderSwitch(finalCompiledExpression, replacementMap, finalDefaultReplacement), TypeToken.STRING, finalCompiledExpression.requiresViewerContext() || finalDefaultReplacement.requiresViewerContext() || replacementMap.values().stream().anyMatch(TextTemplate::requiresViewerContext));
        }
    }

    @NoArgsConstructor
    public static class Compute extends CustomPlaceholderConfiguration {

        @Getter
        @Setter
        private MarkedStringProperty expression;

        private transient boolean needToFixMark;

        public Compute(String text) {
            if (text != null) {
                this.expression = new MarkedStringProperty(text);
            }
            this.needToFixMark = true;
        }

        @Override
        public void setStartMark(Mark startMark) {
            super.setStartMark(startMark);
            if (needToFixMark) {
                this.expression.setStartMark(startMark);
            }
        }

        @Override
        public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
            ExpressionTemplate compiledExpression = ConstantExpressionTemplate.of(""); // dummy value, to continue processing in case of config errors, to find more errors
            if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !compute", "expression", expression, getStartMark())) {
                try {
                    compiledExpression = tcc.getExpressionEngine().compile(tcc, replaceParameters(expression.getValue(), args), expression.getStartMark());
                } catch (Exception e) {
                    tcc.getErrorHandler().addError("Failed to compile condition for custom placeholder. " + e.getMessage(), expression.getStartMark());
                }
            }
            ExpressionTemplate finalCompiledExpression = compiledExpression;
            return builder.acquireData(() -> new CustomPlaceholderCompute(finalCompiledExpression), TypeToken.DOUBLE, finalCompiledExpression.requiresViewerContext());
        }
    }

    public static class Animated extends CustomPlaceholderConfiguration {

        @Getter
        @Setter
        private MarkedListProperty<MarkedStringProperty> elements;

        @Getter
        @Setter
        private MarkedFloatProperty interval;

        @Override
        public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
            List<TextTemplate> elementTemplates = new ArrayList<>(elements.size());
            if ((ConfigValidationUtil.checkNotNull(tcc, "!animated custom placeholder", "elements", elements, getStartMark())
                    && ConfigValidationUtil.checkNotEmpty(tcc, "!animated custom placeholder", "elements", elements, elements.getStartMark()))
                    & ConfigValidationUtil.checkNotNull(tcc, "!animated custom placeholder", "interval", interval, getStartMark())
                    && ConfigValidationUtil.checkRange(tcc, "!animated custom placeholder", "interval", interval.getValue(), 0.05f, 9999f, interval.getStartMark())) {

                for (MarkedStringProperty element : elements) {
                    if (element == null) {
                        elementTemplates.add(TextTemplate.EMPTY);
                    } else {
                        elementTemplates.add(TextTemplate.parse(replaceParameters(element.getValue(), args), element.getStartMark(), tcc));
                    }
                }
            }
            return builder.acquireData(() -> new CustomPlaceholderAnimated(elementTemplates, interval.getValue()), TypeToken.STRING, elementTemplates.stream().anyMatch(TextTemplate::requiresViewerContext));
        }
    }

    public static class Alias extends CustomPlaceholderConfiguration {

        @Getter
        private String replacement;

        public Alias(String replacement) {
            this.replacement = replacement;
        }

        @Override
        public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
            TextTemplate textTemplate = TextTemplate.parse(replacement, getStartMark(), tcc);
            return builder.acquireData(() -> new CustomPlaceholderAlias(textTemplate), TypeToken.STRING, textTemplate.requiresViewerContext());
        }
    }
}
