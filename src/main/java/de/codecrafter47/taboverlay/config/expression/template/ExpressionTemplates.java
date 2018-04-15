package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.Expressions;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import lombok.EqualsAndHashCode;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class ExpressionTemplates {

    public  ExpressionTemplate negate(ExpressionTemplate template) {
        return new Negation(template);
    }

    public  ExpressionTemplate and(Collection<ExpressionTemplate> operands) {
        return new And(operands);
    }

    public  ExpressionTemplate or(Collection<ExpressionTemplate> operands) {
        return new Or(operands);
    }

    public  ExpressionTemplate concat(Collection<ExpressionTemplate> operands) {
        return new Concatenate(operands);
    }

    public  ExpressionTemplate equal(ExpressionTemplate a, ExpressionTemplate b) {
        return new Equal(a, b);
    }

    public  ExpressionTemplate notEqual(ExpressionTemplate a, ExpressionTemplate b) {
        return new NotEqual(a, b);
    }

    public  ExpressionTemplate greater(ExpressionTemplate a, ExpressionTemplate b) {
        return new Greater(a, b);
    }

    public  ExpressionTemplate greaterOrEqual(ExpressionTemplate a, ExpressionTemplate b) {
        return new GreaterOrEqual(a, b);
    }

    public  ExpressionTemplate less(ExpressionTemplate a, ExpressionTemplate b) {
        return new Less(a, b);
    }

    public  ExpressionTemplate lessOrEqual(ExpressionTemplate a, ExpressionTemplate b) {
        return new LessOrEqual(a, b);
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Negation extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate template;

        Negation(ExpressionTemplate template) {
            this.template = template;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.negate(template.instantiateWithBooleanResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return template.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class And extends AbstractBooleanExpressionTemplate {
        private final Collection<ExpressionTemplate> operands;

        And(Collection<ExpressionTemplate> operands) {
            this.operands = operands;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.and(operands.stream().map(ExpressionTemplate::instantiateWithBooleanResult).collect(Collectors.toList()));
        }

        @Override
        public boolean requiresViewerContext() {
            return operands.stream().anyMatch(ExpressionTemplate::requiresViewerContext);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Or extends AbstractBooleanExpressionTemplate {
        private final Collection<ExpressionTemplate> operands;

        Or(Collection<ExpressionTemplate> operands) {
            this.operands = operands;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.or(operands.stream().map(ExpressionTemplate::instantiateWithBooleanResult).collect(Collectors.toList()));
        }

        @Override
        public boolean requiresViewerContext() {
            return operands.stream().anyMatch(ExpressionTemplate::requiresViewerContext);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Concatenate extends AbstractStringExpressionTemplate {
        private final Collection<ExpressionTemplate> operands;

        Concatenate(Collection<ExpressionTemplate> operands) {
            this.operands = operands;
        }

        @Override
        public ToStringExpression instantiateWithStringResult() {
            return Expressions.concat(operands.stream().map(ExpressionTemplate::instantiateWithStringResult).collect(Collectors.toList()));
        }

        @Override
        public boolean requiresViewerContext() {
            return operands.stream().anyMatch(ExpressionTemplate::requiresViewerContext);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Equal extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        Equal(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.equal(a.instantiateWithStringResult(), b.instantiateWithStringResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class NotEqual extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        NotEqual(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.notEqual(a.instantiateWithStringResult(), b.instantiateWithStringResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Greater extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        Greater(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.greaterThan(a.instantiateWithDoubleResult(), b.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class GreaterOrEqual extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        GreaterOrEqual(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.greaterOrEqualThan(a.instantiateWithDoubleResult(), b.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Less extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        Less(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.lesserThan(a.instantiateWithDoubleResult(), b.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class LessOrEqual extends AbstractBooleanExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        LessOrEqual(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Expressions.lesserOrEqualThan(a.instantiateWithDoubleResult(), b.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }
}
