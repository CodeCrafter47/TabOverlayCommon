package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderConditional;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderSwitch;
import de.codecrafter47.taboverlay.config.placeholder.Placeholder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class CustomPlaceholderConfiguration extends MarkedPropertyBase {

    private MarkedIntegerProperty parameters = new MarkedIntegerProperty(0);

    public abstract Placeholder bindArgs(TemplateCreationContext tcc, String[] args);

    String replaceParameters(String template, String[] args) {
        for (int i = 0; i < parameters.getValue(); i++) {
            String replacement;
            if (i < args.length) {
                replacement = args[i];
                if (i == parameters.getValue() - 1) {
                    for (int j = i + 1; j < args.length; j++) {
                        replacement += " " + args[j];
                    }
                }
            } else {
                replacement = "";
            }
            template = template.replace("%" + i, replacement);
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
        public Placeholder bindArgs(TemplateCreationContext tcc, String[] args) {
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
            return new CustomPlaceholderConditional(compiledCondition,
                    trueReplacement,
                    falseReplacement);
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
        public Placeholder bindArgs(TemplateCreationContext tcc, String[] args) {
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
                    }}
            }
            TextTemplate defaultReplacement = TextTemplate.EMPTY;
            if (this.defaultReplacement != null) {
                defaultReplacement = TextTemplate.parse(replaceParameters(this.defaultReplacement.getValue(), args), this.defaultReplacement.getStartMark(), tcc);
            }
            return new CustomPlaceholderSwitch(compiledExpression, replacementMap, defaultReplacement);
        }
    }
}
