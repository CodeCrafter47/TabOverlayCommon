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
        tcc = new TemplateCreationContext(expressionEngine, null, null, null, null);
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
        Assert.assertFalse(expression.instantiateWithBooleanResult().evaluate());
        Assert.assertEquals(4, expression.instantiateWithDoubleResult().evaluate(), 0.001);
        Assert.assertEquals("4", expression.instantiateWithStringResult().evaluate());

        expression = expressionEngine.compile(tcc, "\"four\"", null);
        Assert.assertFalse(expression.instantiateWithBooleanResult().evaluate());
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
}