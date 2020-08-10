package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.misc.ChatFormat;
import de.codecrafter47.taboverlay.config.misc.TextColor;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CustomPlaceholderProgressBar extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener {

    private final ToDoubleExpression value;
    private final ExpressionTemplate valueTemplate;
    private final ToDoubleExpression minValue;
    private final ExpressionTemplate minValueTemplate;
    private final ToDoubleExpression maxValue;
    private final ExpressionTemplate maxValueTemplate;
    private final ExpressionTemplate percentageTemplate;
    private final int symbolCount;
    private final String symbolCompleted;
    private final String symbolRemaining;
    private final List<String> symbolsCurrent;
    private final TextView borderLeft;
    private final TextView borderRight;
    private final BarRenderer regularBarRenderer;
    private final BarRenderer emptyBarRenderer;
    private final BarRenderer fullBarRenderer;
    private BarRenderer activeBarRenderer;
    private String bar;

    public CustomPlaceholderProgressBar(ExpressionTemplate valueTemplate, ExpressionTemplate minValueTemplate, ExpressionTemplate maxValueTemplate, int symbolCount, String symbolCompleted, String symbolRemaining, List<String> symbolsCurrent, TextTemplate borderLeftTemplate, TextTemplate borderRightTemplate, TextTemplate textCenterTemplate, TextTemplate textCenterEmptyTemplate, TextTemplate textCenterFullTemplate, CustomPlaceholderProgressBar.BarColor colorCompleted, @Nullable TextColor colorCurrent, boolean colorCurrentInterpolate, CustomPlaceholderProgressBar.BarColor colorRemaining, TextColor colorEmptyBar, TextColor colorFullBar, boolean emptyBarShowSymbols, boolean fullBarShowSymbols, TextTemplate emptyBarTemplate, TextTemplate fullBarTemplate) {
        this.value = valueTemplate.instantiateWithDoubleResult();
        this.valueTemplate = valueTemplate;
        this.minValue = minValueTemplate.instantiateWithDoubleResult();
        this.minValueTemplate = minValueTemplate;
        this.maxValue = maxValueTemplate.instantiateWithDoubleResult();
        this.maxValueTemplate = maxValueTemplate;
        this.percentageTemplate = ExpressionTemplates.product(Arrays.asList(ExpressionTemplates.div(ExpressionTemplates.sub(valueTemplate, minValueTemplate), ExpressionTemplates.sub(maxValueTemplate, minValueTemplate)), ConstantExpressionTemplate.of(100)));
        this.symbolCount = symbolCount;
        this.symbolCompleted = symbolCompleted;
        this.symbolRemaining = symbolRemaining;
        this.symbolsCurrent = symbolsCurrent;
        this.borderLeft = borderLeftTemplate.instantiate();
        this.borderRight = borderRightTemplate.instantiate();

        double symbolLength = ChatFormat.formattedTextLength(symbolCompleted);
        if (textCenterTemplate == null) {
            this.regularBarRenderer = new PlainBarRenderer(colorCompleted, colorCurrent, colorCurrentInterpolate, colorRemaining);
        } else {
            this.regularBarRenderer = new CenterTextBarRenderer(colorCompleted, colorCurrent, colorCurrentInterpolate, colorRemaining, textCenterTemplate.instantiate(), symbolLength * symbolCount);
        }

        if (emptyBarTemplate != null) {
            this.emptyBarRenderer = new TextViewBarRenderer(emptyBarTemplate.instantiate());
        } else if (colorEmptyBar != null || textCenterEmptyTemplate != null || !emptyBarShowSymbols) {
            BarColor colorCompleted1 = colorEmptyBar == null ? colorCompleted : new ConstantBarColor(colorEmptyBar);
            TextColor colorCurrent1 = colorEmptyBar == null ? colorCurrent : colorEmptyBar;
            boolean colorCurrentInterpolate1 = colorEmptyBar == null ? colorCurrentInterpolate : false;
            BarColor colorRemaining1 = colorEmptyBar == null ? colorRemaining : new ConstantBarColor(colorEmptyBar);
            if (textCenterEmptyTemplate == null && textCenterTemplate == null && emptyBarShowSymbols) {
                this.emptyBarRenderer = new PlainBarRenderer(colorCompleted1, colorCurrent1, colorCurrentInterpolate1, colorRemaining1);
            } else {
                TextTemplate textCenterTemplate1 = textCenterEmptyTemplate != null ? textCenterEmptyTemplate : textCenterTemplate != null ? textCenterTemplate : TextTemplate.EMPTY;
                this.emptyBarRenderer = new CenterTextBarRenderer(colorCompleted1, colorCurrent1, colorCurrentInterpolate1, colorRemaining1, textCenterTemplate1.instantiate(), symbolLength * symbolCount);
            }
        } else {
            this.emptyBarRenderer = this.regularBarRenderer;
        }

        if (fullBarTemplate != null) {
            this.fullBarRenderer = new TextViewBarRenderer(fullBarTemplate.instantiate());
        } else if (colorFullBar != null || textCenterFullTemplate != null || !fullBarShowSymbols) {
            BarColor colorCompleted1 = colorFullBar == null ? colorCompleted : new ConstantBarColor(colorFullBar);
            TextColor colorCurrent1 = colorFullBar == null ? colorCurrent : colorFullBar;
            boolean colorCurrentInterpolate1 = colorFullBar == null ? colorCurrentInterpolate : false;
            BarColor colorRemaining1 = colorFullBar == null ? colorRemaining : new ConstantBarColor(colorFullBar);
            if (textCenterFullTemplate == null && textCenterTemplate == null && fullBarShowSymbols) {
                this.fullBarRenderer = new PlainBarRenderer(colorCompleted1, colorCurrent1, colorCurrentInterpolate1, colorRemaining1);
            } else {
                TextTemplate textCenterTemplate1 = textCenterFullTemplate != null ? textCenterFullTemplate : textCenterTemplate != null ? textCenterTemplate : TextTemplate.EMPTY;
                this.fullBarRenderer = new CenterTextBarRenderer(colorCompleted1, colorCurrent1, colorCurrentInterpolate1, colorRemaining1, textCenterTemplate1.instantiate(), symbolLength * symbolCount);
            }
        } else {
            this.fullBarRenderer = this.regularBarRenderer;
        }
    }

    @Override
    public String getData() {
        return bar;
    }

    @Override
    protected void onActivation() {
        value.activate(getContext(), this);
        minValue.activate(getContext(), this);
        maxValue.activate(getContext(), this);
        updateActiveBarRenderer();
        renderBar();
    }

    @Override
    protected void onDeactivation() {
        value.deactivate();
        minValue.deactivate();
        maxValue.deactivate();
        activeBarRenderer.deactivate();
        activeBarRenderer = null;
    }

    @Override
    public void onExpressionUpdate() {
        updateActiveBarRenderer();
        renderBar();
        notifyListeners();
    }

    private void notifyListeners() {
        if (hasListener()) {
            getListener().run();
        }
    }

    private void updateActiveBarRenderer() {
        BarRenderer renderer = regularBarRenderer;
        double value = this.value.evaluate();
        if (value <= this.minValue.evaluate()) {
            renderer = emptyBarRenderer;
        }
        if (value >= this.maxValue.evaluate()) {
            renderer = fullBarRenderer;
        }
        if (renderer != this.activeBarRenderer) {
            if (this.activeBarRenderer != null) {
                this.activeBarRenderer.deactivate();
            }
            this.activeBarRenderer = renderer;
            this.activeBarRenderer.activate();
        }
    }

    private void renderBar() {
        bar = activeBarRenderer.render(value.evaluate(), minValue.evaluate(), maxValue.evaluate());
    }

    private Context prepareBarContext() {
        Context context = getContext().clone();
        context.setCustomObject(ContextKeys.BAR_VALUE, valueTemplate);
        context.setCustomObject(ContextKeys.BAR_PERCENTAGE, percentageTemplate);
        context.setCustomObject(ContextKeys.BAR_MIN_VALUE, minValueTemplate);
        context.setCustomObject(ContextKeys.BAR_MAX_VALUE, maxValueTemplate);
        return context;
    }

    private interface BarRenderer {

        String render(double value, double minValue, double maxValue);

        void activate();

        void deactivate();
    }

    private class PlainBarRenderer implements BarRenderer, TextViewUpdateListener {

        private final BarColor colorCompleted;
        @Nullable
        private final TextColor colorCurrent;
        private final boolean colorCurrentInterpolate;
        private final BarColor colorRemaining;

        public PlainBarRenderer(BarColor colorCompleted, @Nullable TextColor colorCurrent, boolean colorCurrentInterpolate, BarColor colorRemaining) {

            this.colorCompleted = colorCompleted;
            this.colorCurrent = colorCurrent;
            this.colorCurrentInterpolate = colorCurrentInterpolate;
            this.colorRemaining = colorRemaining;
        }

        @Override
        public String render(double value, double minValue, double maxValue) {
            double p = (value - minValue) / (maxValue - minValue);
            int idxCurrent = (int) (p * symbolCount);
            if (p <= 0) {
                idxCurrent = -1;
            }
            if (p >= 1) {
                idxCurrent = symbolCount;
            }

            StringBuilder sb = new StringBuilder(bar != null ? bar.length() : 64);

            sb.append(borderLeft.getText());

            // completed progress
            int i;
            for (i = 0; i < idxCurrent && i < symbolCount; i++) {
                sb.append(colorCompleted.getFormat(i / (float) symbolCount).getFormatCode());
                sb.append(symbolCompleted);
            }
            // current progress
            if (i < symbolCount && i == idxCurrent) {
                double pi = p * symbolCount - i;
                if (colorCurrentInterpolate) {
                    sb.append(TextColor.interpolateLinear(colorRemaining.getFormat(p), colorCompleted.getFormat(p), pi).getFormatCode());
                } else if (colorCurrent != null) {
                    sb.append(colorCurrent.getFormatCode());
                } else {
                    sb.append(colorCompleted.getFormat(p).getFormatCode());
                }
                int si = (int) (pi * symbolsCurrent.size());
                if (si < 0) {
                    si = 0;
                } else if (si >= symbolsCurrent.size()) {
                    si = symbolsCurrent.size() - 1;
                }
                sb.append(symbolsCurrent.get(si));
                i++;
            }
            // remaining progress
            for (;i < symbolCount; i++) {
                sb.append(colorRemaining.getFormat(i / (float) symbolCount).getFormatCode());
                sb.append(symbolRemaining);
            }
            sb.append("&r");
            sb.append(borderRight.getText());

            return sb.toString();
        }

        @Override
        public void activate() {
            Context context = prepareBarContext();
            borderLeft.activate(context, this);
            borderRight.activate(context, this);
        }

        @Override
        public void deactivate() {
            borderLeft.deactivate();
            borderRight.deactivate();
        }

        @Override
        public void onTextUpdated() {
            renderBar();
            notifyListeners();
        }
    }

    private class CenterTextBarRenderer implements BarRenderer, TextViewUpdateListener {

        private final BarColor colorCompleted;
        @Nullable
        private final TextColor colorCurrent;
        private final boolean colorCurrentInterpolate;
        private final BarColor colorRemaining;
        private final TextView centerText;
        private final double length;

        public CenterTextBarRenderer(BarColor colorCompleted, @Nullable TextColor colorCurrent, boolean colorCurrentInterpolate, BarColor colorRemaining, TextView centerText, double length) {
            this.colorCompleted = colorCompleted;
            this.colorCurrent = colorCurrent;
            this.colorCurrentInterpolate = colorCurrentInterpolate;
            this.colorRemaining = colorRemaining;
            this.centerText = centerText;
            this.length = length;
        }

        @Override
        public String render(double value, double minValue, double maxValue) {

            String text = centerText.getText();
            double textLength = ChatFormat.formattedTextLength(text);
            double symbolLength = ChatFormat.formattedTextLength(symbolCompleted);

            int symbolCountLeft = (int) ((length - textLength) / symbolLength / 2);
            int symbolCountRight = (int) ((length - textLength - symbolCountLeft * symbolLength) / symbolLength);
            text = text + ChatFormat.stripFormat(ChatFormat.createSpacesExact((float) (length - textLength - (symbolCountLeft + symbolCountRight) * symbolLength)));

            if (symbolCountLeft < 0) {
                symbolCountLeft = 0;
            }
            if (symbolCountRight < 0) {
                symbolCountRight = 0;
            }

            double p = (value - minValue) / (maxValue - minValue);
            int idxCurrent = (int) (p * length);
            if (p <= 0) {
                idxCurrent = -1;
            }
            if (p >= 1) {
                idxCurrent = (int) length;
            }

            StringBuilder sb = new StringBuilder(bar != null ? bar.length() : 64);

            sb.append(borderLeft.getText());

            // left bar
            // completed progress
            int i;
            double l;
            for (i = 0, l = 0; idxCurrent >= l + symbolLength && i < symbolCountLeft; i++, l+=symbolLength) {
                sb.append(colorCompleted.getFormat(l / length).getFormatCode());
                sb.append(symbolCompleted);
            }
            // current progress
            if (i < symbolCountLeft && idxCurrent >= l && idxCurrent < l + symbolLength) {
                double pi = p * symbolCount - i;
                if (colorCurrentInterpolate) {
                    sb.append(TextColor.interpolateLinear(colorRemaining.getFormat(p), colorCompleted.getFormat(p), pi).getFormatCode());
                } else if (colorCurrent != null) {
                    sb.append(colorCurrent.getFormatCode());
                } else {
                    sb.append(colorCompleted.getFormat(p).getFormatCode());
                }
                int si = (int) pi;
                if (si < 0) {
                    si = 0;
                } else if (si >= symbolsCurrent.size()) {
                    si = symbolsCurrent.size() - 1;
                }
                sb.append(symbolsCurrent.get(si));
                i++;
                l+=symbolLength;
            }
            // remaining progress
            for (;i < symbolCountLeft; i++,l+=symbolLength) {
                sb.append(colorRemaining.getFormat(l / length).getFormatCode());
                sb.append(symbolRemaining);
            }
            sb.append("&r");

            // center text
            // completed progress
            int c;
            for (i = 0; i < text.length() && idxCurrent >= l + ChatFormat.getCharWidth(c = text.codePointAt(i)); i+=Character.charCount(c), l+=ChatFormat.getCharWidth(c)) {
                sb.append(colorCompleted.getFormat(l / length).getFormatCode());
                sb.appendCodePoint(c);
            }
            // current progress
            if (i < text.length() && l <= idxCurrent && l + symbolLength > ChatFormat.getCharWidth(c = text.codePointAt(i))) {
                double pi = (p * length - l) / ChatFormat.getCharWidth(c);
                if (colorCurrentInterpolate) {
                    sb.append(TextColor.interpolateLinear(colorRemaining.getFormat(p), colorCompleted.getFormat(p), pi).getFormatCode());
                } else if (colorCurrent != null) {
                    sb.append(colorCurrent.getFormatCode());
                } else {
                    sb.append(colorCompleted.getFormat(p).getFormatCode());
                }
                sb.appendCodePoint(c);
                i++;
                l+=ChatFormat.getCharWidth(c);
            }
            // remaining progress
            for (;i < text.length(); i+=Character.charCount(c), l+=ChatFormat.getCharWidth(c)) {
                c = text.codePointAt(i);
                sb.append(colorRemaining.getFormat(l / length).getFormatCode());
                sb.appendCodePoint(c);
            }
            sb.append("&r");

            // right bar
            for (i = symbolCount - symbolCountRight; idxCurrent >= l + symbolLength && i < symbolCount; i++, l+=symbolLength) {
                sb.append(colorCompleted.getFormat(l / length).getFormatCode());
                sb.append(symbolCompleted);
            }
            // current progress
            if (i < symbolCount && l <= idxCurrent && l + symbolLength > idxCurrent) {
                double pi = p * symbolCount - i;
                if (colorCurrentInterpolate) {
                    sb.append(TextColor.interpolateLinear(colorRemaining.getFormat(p), colorCompleted.getFormat(p), pi).getFormatCode());
                } else if (colorCurrent != null) {
                    sb.append(colorCurrent.getFormatCode());
                } else {
                    sb.append(colorCompleted.getFormat(p).getFormatCode());
                }
                int si = (int) pi;
                if (si < 0) {
                    si = 0;
                } else if (si >= symbolsCurrent.size()) {
                    si = symbolsCurrent.size() - 1;
                }
                sb.append(symbolsCurrent.get(si));
                i++;
                l+=symbolLength;
            }
            // remaining progress
            for (;i < symbolCount; i++,l+=symbolLength) {
                sb.append(colorRemaining.getFormat(l / length).getFormatCode());
                sb.append(symbolRemaining);
            }
            sb.append("&r");

            sb.append(borderRight.getText());

            return sb.toString();
        }

        @Override
        public void activate() {
            Context context = prepareBarContext();
            borderLeft.activate(context, this);
            borderRight.activate(context, this);
            centerText.activate(context, this);
        }

        @Override
        public void deactivate() {
            borderLeft.deactivate();
            borderRight.deactivate();
            centerText.deactivate();
        }

        @Override
        public void onTextUpdated() {
            renderBar();
            notifyListeners();
        }
    }

    private class TextViewBarRenderer implements BarRenderer, TextViewUpdateListener {
        private final TextView textView;

        private TextViewBarRenderer(TextView textView) {
            this.textView = textView;
        }

        @Override
        public String render(double value, double minValue, double maxValue) {
            return textView.getText();
        }

        @Override
        public void activate() {
            Context context = prepareBarContext();
            textView.activate(context, this);
        }

        @Override
        public void deactivate() {
            textView.deactivate();
        }

        @Override
        public void onTextUpdated() {
            renderBar();
            notifyListeners();
        }
    }

    public interface BarColor {

        BarColor NONE = new BarColor() {
            @Override
            public TextColor getFormat(double progress) {
                return TextColor.COLOR_WHITE;
            }
        };

        /**
         * Get the format code corresponding to the progress.
         *
         * @param progress between 0 and 100
         * @return format code
         */
        TextColor getFormat(double progress);
    }

    @AllArgsConstructor
    public static class ConstantBarColor implements BarColor {
        private final TextColor color;

        @Override
        public TextColor getFormat(double progress) {
            return color;
        }
    }

    @AllArgsConstructor
    public static class StepBarColor implements BarColor {
        int[] colorCompletedSteps;
        TextColor[] colorCompletedColors;

        @Override
        public TextColor getFormat(double progress) {
            int p = (int) (progress * 100);
            int i = Arrays.binarySearch(colorCompletedSteps, p);
            if (i < 0) {
                i = -i - 2;
            }
            if (i < 0) {
                i = 0;
            }
            if (i >= colorCompletedSteps.length) {
                i = colorCompletedSteps.length - 1;
            }
            return colorCompletedColors[i];
        }
    }

    @AllArgsConstructor
    public static class InterpolateBarColor implements BarColor {
        int[] colorCompletedSteps;
        TextColor[] colorCompletedColors;

        @Override
        public TextColor getFormat(double progress) {
            int p = (int) (progress * 100);
            int i = Arrays.binarySearch(colorCompletedSteps, p);
            if (i < 0) {
                i = -i - 2;
            }
            if (i < 0) {
                return colorCompletedColors[0];
            }
            if (i >= colorCompletedSteps.length - 1) {
                return colorCompletedColors[colorCompletedSteps.length - 1];
            }
            return TextColor.interpolateSine(colorCompletedColors[i],
                    colorCompletedColors[i + 1],
                    (progress * 100 - colorCompletedSteps[i]) / (colorCompletedSteps[i + 1] - colorCompletedSteps[i]))
                    ;
        }
    }

    @AllArgsConstructor
    public static class DarkerBarColor implements BarColor {
        private BarColor other;

        @Override
        public TextColor getFormat(double progress) {
            TextColor color = other.getFormat(progress);
            return new TextColor(color.getR() / 2, color.getG() / 2, color.getB() / 2);
        }
    }
}
