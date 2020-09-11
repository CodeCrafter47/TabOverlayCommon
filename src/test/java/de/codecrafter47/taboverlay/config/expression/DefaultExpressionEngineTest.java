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

package de.codecrafter47.taboverlay.config.expression;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by florian on 10/22/16.
 */
public class DefaultExpressionEngineTest {

    ExpressionEngine expressionEngine;
    TemplateCreationContext tcc;

    @Before
    public void setup() {
        expressionEngine = new DefaultExpressionEngine(DefaultExpressionEngine.Options.builder().withDefaultOperators().withDefaultTokenReaders().withDefaultValueReaders().build());
        tcc = new TemplateCreationContext(expressionEngine, null, null, null, null, null);
    }

    private void assertTrue(String expr) {
        Assert.assertTrue(expressionEngine.compile(tcc, expr, null).instantiateWithBooleanResult().evaluate());
    }

    private void assertFalse(String expr) {
        Assert.assertFalse(expressionEngine.compile(tcc, expr, null).instantiateWithBooleanResult().evaluate());
    }

    @Test
    public void testExpressions() {
        ExpressionTemplate expression = expressionEngine.compile(tcc, "4", null);
        Assert.assertEquals(4, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("4", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "\"four\"", null);
        Assert.assertEquals(4, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("four", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "\"true\"", null);
        Assert.assertTrue(expression.instantiateWithBooleanResult().evaluate());
        Assert.assertEquals(4, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("true", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "true", null);
        Assert.assertTrue(expression.instantiateWithBooleanResult().evaluate());
        Assert.assertEquals(1, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("true", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "all", null);
        Assert.assertTrue(expression.instantiateWithBooleanResult().evaluate());
        Assert.assertEquals(1, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("true", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "false", null);
        Assert.assertFalse(expression.instantiateWithBooleanResult().evaluate());
        Assert.assertEquals(0, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("false", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "\"A\" . \"B\"", null);
        Assert.assertEquals("AB", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "4.25", null);
        Assert.assertEquals(4.25, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("4.25", expression.instantiateWithStringResult().evaluate());
    }

    @Test
    public void testNegativeNumbers() {
        ExpressionTemplate expression = expressionEngine.compile(tcc, "-1", null);
        Assert.assertEquals(-1, expression.instantiateWithDoubleResult().evaluate(), 0.001);
    }

    @Test
    public void testBooleanExpressions() {
        assertTrue("1 == 1");
        assertTrue("\"test\" == \"test\"");
        assertFalse("\"test\" != \"test\"");
        assertFalse("\"test\" == \"abc\"");
        assertTrue("\"test\" != \"abc\"");
        assertTrue("1 < 2 < 3");
        assertTrue("1 < 2 <= 3");
        assertFalse("1 < 2 <= 1");
        assertFalse("1 < 2 && 2 < 1");
        assertTrue("1 < 2");
        assertTrue("2 < 3");
        assertTrue("true");
        assertTrue("true && true && true");
        assertTrue("1 < 2 && 2 < 3 && true");
        assertTrue("true || false || true");
        assertTrue("true || false || false");
        assertTrue("false || false || true");
        assertFalse("false || false || false");
        assertFalse("false && false && false");
        assertFalse("true && false && true");
        assertTrue("true && true && true");
        assertTrue("true && true && false || true");
    }

    @Test
    public void testArithmeticExpressions() {
        assertTrue("1 + 1 == 2");
        assertTrue("1 + 1 + 1 == 3");
        assertTrue("1 + 1 - 1 == 1");
        assertTrue("(4/2) == 2");
        assertTrue("(2*2) == 4");
    }
}