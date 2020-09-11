/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.Expressions;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import lombok.EqualsAndHashCode;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.Function;
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

    public ExpressionTemplate sum(Collection<ExpressionTemplate> operands) {
        return new Sum(operands);
    }

    public ExpressionTemplate product(Collection<ExpressionTemplate> operands) {
        return new Product(operands);
    }
    public  ExpressionTemplate sub(ExpressionTemplate a, ExpressionTemplate b) {
        return new Sub(a, b);
    }

    public ExpressionTemplate div(ExpressionTemplate a, ExpressionTemplate b) {
        return new Div(a, b);
    }

    public ExpressionTemplate negateNumber(ExpressionTemplate template) {
        return new NegationNumber(template);
    }

    public ExpressionTemplate applyStringToStringFunction(ExpressionTemplate template, Function<String, String> function) {
        return new ApplyStringToStringFunction(template, function);
    }

    ;

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

    @EqualsAndHashCode(callSuper = false)
    private static class Sum extends AbstractDoubleExpressionTemplate {
        private final Collection<ExpressionTemplate> operands;

        Sum(Collection<ExpressionTemplate> operands) {
            this.operands = operands;
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return Expressions.sum(operands.stream().map(ExpressionTemplate::instantiateWithDoubleResult).collect(Collectors.toList()));
        }

        @Override
        public boolean requiresViewerContext() {
            return operands.stream().anyMatch(ExpressionTemplate::requiresViewerContext);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Product extends AbstractDoubleExpressionTemplate {
        private final Collection<ExpressionTemplate> operands;

        Product(Collection<ExpressionTemplate> operands) {
            this.operands = operands;
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return Expressions.product(operands.stream().map(ExpressionTemplate::instantiateWithDoubleResult).collect(Collectors.toList()));
        }

        @Override
        public boolean requiresViewerContext() {
            return operands.stream().anyMatch(ExpressionTemplate::requiresViewerContext);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Sub extends AbstractDoubleExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        Sub(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return Expressions.sub(a.instantiateWithDoubleResult(), b.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class Div extends AbstractDoubleExpressionTemplate {
        private final ExpressionTemplate a;
        private final ExpressionTemplate b;

        Div(ExpressionTemplate a, ExpressionTemplate b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return Expressions.div(a.instantiateWithDoubleResult(), b.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return a.requiresViewerContext() || b.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class NegationNumber extends AbstractDoubleExpressionTemplate {
        private final ExpressionTemplate template;

        NegationNumber(ExpressionTemplate template) {
            this.template = template;
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return Expressions.negateNumber(template.instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return template.requiresViewerContext();
        }
    }

    @EqualsAndHashCode(callSuper = false)
    private static class ApplyStringToStringFunction extends AbstractStringExpressionTemplate {
        private final ExpressionTemplate template;
        private final Function<String, String> function;

        ApplyStringToStringFunction(ExpressionTemplate template, Function<String, String> function) {
            this.template = template;
            this.function = function;
        }

        @Override
        public ToStringExpression instantiateWithStringResult() {
            return Expressions.applyToStringFunction(template.instantiateWithStringResult(), function);
        }

        @Override
        public boolean requiresViewerContext() {
            return template.requiresViewerContext();
        }
    }

}
