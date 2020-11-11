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

package de.codecrafter47.taboverlay.config.dsl.customplaceholder;

import com.google.common.collect.ImmutableMap;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.*;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.misc.ChatFormat;
import de.codecrafter47.taboverlay.config.misc.TextColor;
import de.codecrafter47.taboverlay.config.placeholder.BarPlaceholderResolver;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderProgressBar;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.*;

@Getter
@Setter
public class CustomPlaceholderProgressBarConfiguration extends CustomPlaceholderConfiguration {

    /**
     * Expression for the number the progress bar will be based on.
     */
    private MarkedStringProperty value;

    /**
     * Expression for the number corresponding to 0%.
     */
    private MarkedStringProperty minValue;

    /**
     * Expression for the number corresponding to 100%.
     */
    private MarkedStringProperty maxValue;

    /**
     * Style preset. This options allows you use one of the pre-made styles.
     * Can be one of
     * - default
     * - largediv
     * - tricolor
     * - default_shaded
     * - largediv_shaded
     * - tricolor_shaded
     * - hearts
     * Shaded variants only work for Minecraft 1.16 or later.
     */
    private MarkedStringProperty style;

    /**
     * Length of the progress bar (number of symbols the progress bar is made up of).
     */
    private MarkedIntegerProperty length;

    /**
     * Symbol (or text) to be used for completed progress. This should not contain color codes.
     * Color codes should be added using the colorCompleted option (or related options).
     */
    private MarkedStringProperty symbolCompleted;

    /**
     * Symbol (or text) to be used for remaining progress. This should not contain color codes.
     * Color codes should be added using the colorRemaining option (or related options).
     */
    private MarkedStringProperty symbolRemaining;

    /**
     * Symbol (or text) to be used for current progress (head of the progress bar). This should not contain color codes.
     * Color codes should be added using the colorCurrent option (or related options).
     */
    private MarkedStringProperty symbolCurrent;

    /**
     * List of symbols (or text) to be used for current progress. By providing multiple symbols
     * the progress can be divided in smaller steps. E.g. providing a half filled heart and a
     * filled heart will display the half filled heart as an in between step to displaying the
     * filled heart.
     * This should not contain color codes. Color codes should be added using the
     * colorCurrent option (or related options).
     * This option is mutually exclusive with symbolCurrent.
     */
    private MarkedListProperty<MarkedStringProperty> symbolCurrentSteps;

    /**
     * Left border of the progress bar. Does not count towards length.
     */
    private MarkedStringProperty borderLeft;

    /**
     * Right border of the progress bar. Does not count towards length.
     */
    private MarkedStringProperty borderRight;

    /**
     * Text to be displayed on the center of the progress bar. This is optional. If specified
     * it will replace some of the symbols at the middle of the progress bar. You can use it
     * e.g. to display the progress percentage or to display the underlying value of the progress
     * bar. The special placeholders `${progress_percentage}`, `${progress_value}`,
     * `${progress_min}` and `${progress_max}` can be used to access the percentage
     * and value of the progress as well as the minimum and maximum value.
     * This should not contain color codes. The text is colored with colors provided by the
     * colorCompleted, colorRemaining and other color options.
     */
    private MarkedStringProperty textCenter;

    /**
     * Text to be displayed on the center of the progress bar when it is empty, i.e. the
     * progress is 0.
     */
    private MarkedStringProperty textCenterEmpty;

    /**
     * Text to be displayed on the center of the progress bar when it is full, i.e. the
     * progress is 100.
     */
    private MarkedStringProperty textCenterFull;

    /**
     * Color to use for completed progress.
     */
    private MarkedStringProperty colorCompleted;

    /**
     * Specifies the color to use for completed progress. Divides the progress bar into multiple
     * segments with different color. The number is the percentage where the color is first used.
     * E.g. if there are two entries `i: c1` and `j: c2` then the color c1 ist used in the
     * interval from i to j - 1.
     * This option is mutually exclusive with colorCompleted.
     */
    private MarkedMapProperty<MarkedIntegerProperty, MarkedStringProperty> colorCompletedSteps;

    /**
     * If enabled the color for completed progress is interpolated between individual progress points specified by colorCompletedSteps.
     * This can only be used in conjunction with colorCompletedSteps and thus is mutually
     * exclusive to colorCompleted.
     * This option only works on Minecraft 1.16+.
     */
    private MarkedBooleanProperty colorCompletedInterpolateSteps;

    /**
     * Color to use for current progress, i.e. the symbol that is the head of the progress bar.
     * This is optional. If not set it defaults to the value of colorCompleted.
     */
    private MarkedStringProperty colorCurrent;

    /**
     * If enable the color to use for current progress, i.e. the symbol that is the head of the
     * progress bar, is interpolated between the color used for remaining progress and the color
     * used for completed progress.
     * This option is mutually exclusive to colorCurrent.
     * This option only works on Minecraft 1.16+.
     */
    private MarkedBooleanProperty colorCurrentInterpolate;

    /**
     * Color to use for remaining progress. To specify the color for remaining progress there are
     * three options with are mutually exclusive:
     * - colorRemaining
     * - colorRemainingSteps and colorRemainingInterpolateSteps
     * - colorRemainingFromColorCompleted
     */
    private MarkedStringProperty colorRemaining;

    /**
     * Specifies the color to use for remaining progress. Divides the progress bar into multiple
     * segments with different color. The number is the percentage where the color is first used.
     * E.g. if there are two entries `i: c1` and `j: c2` then the color c1 ist used in the
     * interval from i + 1 to j.
     * This option is mutually exclusive with colorRemaining.
     */
    private MarkedMapProperty<MarkedIntegerProperty, MarkedStringProperty> colorRemainingSteps;

    /**
     * If enabled the color for remaining progress is interpolated between individual progress points specified by colorRemainingSteps.
     * This can only be used in conjunction with colorRemainingSteps and thus is mutually
     * exclusive to colorRemaining.
     * This option only works on Minecraft 1.16+.
     */
    private MarkedBooleanProperty colorRemainingInterpolateSteps;

    /**
     * If enabled the color for remaining progress is computed as a dark version of the color for completed progress.
     * This option only works on Minecraft 1.16+.
     */
    private MarkedBooleanProperty colorRemainingFromColorCompleted;

    /**
     * Color to use for the bar when it is empty, i.e. the progress is 0. This is optional.
     * If not set it defaults to the color used for remaining progress.
     */
    private MarkedStringProperty colorEmptyBar;

    /**
     * Color to use for the bar when it is full, i.e. the progress is 100. This is optional.
     * If not set it defaults to the color used for completed progress.
     */
    private MarkedStringProperty colorFullBar;

    /**
     * Whether the progress symbols are shown if the bar is empty. If this is set to false the
     * symbols will be replaced with spaces.
     */
    private MarkedBooleanProperty emptyBarShowSymbols;

    /**
     * Whether the progress symbols are shown if the bar is full. If this is set to false the
     * symbols will be replaced with spaces.
     */
    private MarkedBooleanProperty fullBarShowSymbols;

    /**
     * Text to replace the entire progress bar with, if it is empty i.e. the progress is 0.
     * This is optional. If used this text will be shown instead of the entire bar
     * (including the border) when the progress is 0.
     */
    private MarkedStringProperty emptyBar;

    /**
     * Text to replace the entire progress bar with, if it is full i.e. the progress is 100.
     * This is optional. If used this text will be shown instead of the entire bar
     * (including the border) when the progress is 100.
     */
    private MarkedStringProperty fullBar;

    @Override
    public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {

        TemplateCreationContext barContext = tcc.clone();
        barContext.addPlaceholderResolver(new BarPlaceholderResolver());

        ExpressionTemplate value = ConstantExpressionTemplate.of(0);
        if (ConfigValidationUtil.checkNotNull(tcc, "!progress_bar custom placeholder", "value", this.value, getStartMark())) {
            value = tcc.getExpressionEngine().compile(tcc, replaceParameters(this.value.getValue(), args), this.value.getStartMark());
        }
        ExpressionTemplate minValue = ConstantExpressionTemplate.of(0);
        if (this.minValue != null) {
            minValue = tcc.getExpressionEngine().compile(tcc, replaceParameters(this.minValue.getValue(), args), this.minValue.getStartMark());
        }
        ExpressionTemplate maxValue = ConstantExpressionTemplate.of(0);
        if (ConfigValidationUtil.checkNotNull(tcc, "!progress_bar custom placeholder", "maxValue", this.maxValue, getStartMark())) {
            maxValue = tcc.getExpressionEngine().compile(tcc, replaceParameters(this.maxValue.getValue(), args), this.maxValue.getStartMark());
        }

        if (style != null) {
            String symbolCompleted = null;
            String symbolRemaining = null;
            String colorCompleted = null;
            String colorRemaining = null;
            Integer length = null;
            String borderRight = null;
            String borderLeft = null;
            Map<MarkedIntegerProperty, MarkedStringProperty> colorCompletedSteps = null;
            Boolean colorRemainingFromColorCompleted = null;
            Boolean colorCurrentInterpolate = null;
            Boolean colorCompletedInterpolateSteps = null;
            switch (style.getValue()) {
                case "default":
                    symbolCompleted = "\u258D";
                    symbolRemaining = "\u258D";
                    colorCompleted = "&a";
                    colorRemaining = "&8";
                    length = 16;
                    borderRight = "\u2523";
                    borderLeft = "\u252B";
                    break;
                case "largediv":
                    symbolCompleted = "\u2589";
                    symbolRemaining = "\u2589";
                    colorCompleted = "&a";
                    colorRemaining = "&8";
                    length = 8;
                    borderRight = "\u2523";
                    borderLeft = "\u252B";
                    break;
                case "tricolor":
                    symbolCompleted = "\u258D";
                    symbolRemaining = "\u258D";
                    colorCompletedSteps = ImmutableMap.of(
                            new MarkedIntegerProperty(0),
                            new MarkedStringProperty("&a"),
                            new MarkedIntegerProperty(60),
                            new MarkedStringProperty("&e"),
                            new MarkedIntegerProperty(80),
                            new MarkedStringProperty("&c")
                    );
                    colorRemaining = "&8";
                    length = 16;
                    borderRight = "\u2523";
                    borderLeft = "\u252B";
                    break;
                case "default_shaded":
                    symbolCompleted = "\u258D";
                    symbolRemaining = "\u258D";
                    colorCompleted = "&a";
                    colorRemainingFromColorCompleted = true;
                    colorCurrentInterpolate = true;
                    length = 16;
                    borderRight = "\u2523";
                    borderLeft = "\u252B";
                    break;
                case "largediv_shaded":
                    symbolCompleted = "\u2589";
                    symbolRemaining = "\u2589";
                    colorCompleted = "&a";
                    colorRemainingFromColorCompleted = true;
                    colorCurrentInterpolate = true;
                    length = 8;
                    borderRight = "\u2523";
                    borderLeft = "\u252B";
                    break;
                case "tricolor_shaded":
                    symbolCompleted = "\u258D";
                    symbolRemaining = "\u258D";
                    colorCompletedSteps = ImmutableMap.of(
                            new MarkedIntegerProperty(0),
                            new MarkedStringProperty("&a"),
                            new MarkedIntegerProperty(50),
                            new MarkedStringProperty("&a"),
                            new MarkedIntegerProperty(75),
                            new MarkedStringProperty("&e"),
                            new MarkedIntegerProperty(100),
                            new MarkedStringProperty("&c")
                    );
                    colorCompletedInterpolateSteps = true;
                    colorRemainingFromColorCompleted = true;
                    colorCurrentInterpolate = true;
                    length = 16;
                    borderRight = "\u2523";
                    borderLeft = "\u252B";
                    break;
                case "hearts":
                    symbolCompleted = "\u2665";
                    symbolRemaining = "\u2665";
                    colorCompleted = "&c";
                    colorRemaining = "&8";
                    length = 10;
                    break;
                default:
                    tcc.getErrorHandler().addWarning("Unknown value '" + style.getValue() + "' for style option.", this.style.getStartMark());
            }

            if (symbolCompleted != null && this.symbolCompleted == null) {
                this.symbolCompleted = new MarkedStringProperty(symbolCompleted);
            }
            if (symbolRemaining != null && this.symbolRemaining == null) {
                this.symbolRemaining = new MarkedStringProperty(symbolRemaining);
            }
            if (colorCompleted != null && this.colorCompleted == null && this.colorCompletedSteps == null) {
                this.colorCompleted = new MarkedStringProperty(colorCompleted);
            }
            if (colorRemaining != null && this.colorRemaining == null && this.colorRemainingSteps == null && this.colorRemainingFromColorCompleted == null) {
                this.colorRemaining = new MarkedStringProperty(colorRemaining);
            }
            if (length != null && this.length == null) {
                this.length = new MarkedIntegerProperty(length);
            }
            if (borderRight != null && this.borderRight == null) {
                this.borderRight = new MarkedStringProperty(borderRight);
            }
            if (borderLeft != null && this.borderLeft == null) {
                this.borderLeft = new MarkedStringProperty(borderLeft);
            }
            if (colorCompletedSteps != null && this.colorCompletedSteps == null && this.colorCompleted == null) {
                this.colorCompletedSteps = new MarkedMapProperty<>();
                this.colorCompletedSteps.putAll(colorCompletedSteps);
            }
            if (colorRemainingFromColorCompleted != null && this.colorRemaining == null && this.colorRemainingSteps == null && this.colorRemainingInterpolateSteps == null) {
                this.colorRemainingFromColorCompleted = new MarkedBooleanProperty(colorRemainingFromColorCompleted);
            }
            if (colorCurrentInterpolate != null && this.colorCurrent == null && this.colorCurrentInterpolate == null) {
                this.colorCurrentInterpolate = new MarkedBooleanProperty(colorCurrentInterpolate);
            }
            if (colorCompletedInterpolateSteps != null && this.colorCompletedInterpolateSteps == null) {
                this.colorCompletedInterpolateSteps = new MarkedBooleanProperty(colorCompletedInterpolateSteps);
            }
        }


        int length = 10;
        if (this.length != null
                && ConfigValidationUtil.checkRange(tcc, "!progress_bar custom placeholder", "length", this.length.getValue(), 0, 1000, this.length.getStartMark())) {
            length = this.length.getValue();
        }

        boolean symbolsSpecified = true;
        String symbolCompleted = "=";
        if (ConfigValidationUtil.checkNotNull(tcc, "!progress_bar custom placeholder", "symbolCompleted", this.symbolCompleted, getStartMark())) {
            symbolCompleted = replaceParameters(this.symbolCompleted.getValue(), args);
            if (!symbolCompleted.equals(ChatFormat.stripFormat(symbolCompleted))) {
                tcc.getErrorHandler().addWarning("symbolCompleted in !progress_bar should not contain format codes.", this.symbolCompleted.getStartMark());
                symbolsSpecified = false;
            }
        } else {
            symbolsSpecified = false;
        }
        String symbolRemaining = "=";
        if (ConfigValidationUtil.checkNotNull(tcc, "!progress_bar custom placeholder", "symbolRemaining", this.symbolRemaining, getStartMark())) {
            symbolRemaining = replaceParameters(this.symbolRemaining.getValue(), args);
            if (!symbolRemaining.equals(ChatFormat.stripFormat(symbolRemaining))) {
                tcc.getErrorHandler().addWarning("symbolRemaining in !progress_bar should not contain format codes.", this.symbolRemaining.getStartMark());
                symbolsSpecified = false;
            }
        } else {
            symbolsSpecified = false;
        }
        List<String> symbolsCurrent = Collections.singletonList(symbolCompleted);
        if (this.symbolCurrent != null) {
            String symbolCurrent = replaceParameters(this.symbolCurrent.getValue(), args);
            if (!symbolCurrent.equals(ChatFormat.stripFormat(symbolCurrent))) {
                tcc.getErrorHandler().addWarning("symbolCurrent in !progress_bar should not contain format codes.", this.symbolCurrent.getStartMark());
                symbolsSpecified = false;
            }
        }
        if (this.symbolCurrentSteps != null
                && ConfigValidationUtil.checkNotEmpty(tcc, "!progress_bar", "symbolCurrentSteps", this.symbolCurrentSteps, this.symbolCurrentSteps.getStartMark())) {
            if (this.symbolCurrent != null) {
                tcc.getErrorHandler().addWarning("In !progress_bar you cannot use the symbolCurrent and symbolCurrentSteps at the same time. Those are mutually exclusive.", this.symbolCurrentSteps.getStartMark());
            }
            symbolsCurrent = new ArrayList<>();
            for (MarkedStringProperty symbol : this.symbolCurrentSteps) {
                if (symbol != null) {
                    String s = replaceParameters(symbol.getValue(), args);
                    if (!s.equals(ChatFormat.stripFormat(s))) {
                        tcc.getErrorHandler().addWarning("symbolCurrentSteps in !progress_bar should not contain format codes.", symbol.getStartMark());
                        symbolsSpecified = false;
                    }
                    symbolsCurrent.add(s);
                }
            }
        }

        if (symbolsSpecified) {
            // check if they're all the same length
            float symbolCompletedLength = ChatFormat.formattedTextLength(symbolCompleted);

            float diff = symbolCompletedLength - ChatFormat.formattedTextLength(symbolRemaining);
            if (diff < -0.1 || diff > 0.1) {
                tcc.getErrorHandler().addWarning("In !progress_bar symbolCompleted and symbolRemaining have different widths", getStartMark());
            }
        }

        TextTemplate borderLeft = TextTemplate.EMPTY;
        if (this.borderLeft != null) {
            borderLeft = TextTemplate.parse(this.borderLeft.getValue(), this.borderLeft.getStartMark(), barContext);
        }
        TextTemplate borderRight = TextTemplate.EMPTY;
        if (this.borderRight != null) {
            borderRight = TextTemplate.parse(this.borderRight.getValue(), this.borderRight.getStartMark(), barContext);
        }

        TextTemplate textCenter = null;
        if (this.textCenter != null) {
            textCenter = TextTemplate.parse(this.textCenter.getValue(), this.textCenter.getStartMark(), barContext);
        }
        TextTemplate textCenterEmpty = null;
        if (this.textCenterEmpty != null) {
            textCenterEmpty = TextTemplate.parse(this.textCenterEmpty.getValue(), this.textCenterEmpty.getStartMark(), barContext);
        }
        TextTemplate textCenterFull = null;
        if (this.textCenterFull != null) {
            textCenterFull = TextTemplate.parse(this.textCenterFull.getValue(), this.textCenterFull.getStartMark(), barContext);
        }

        CustomPlaceholderProgressBar.BarColor colorCompleted = CustomPlaceholderProgressBar.BarColor.NONE;

        if (this.colorCompleted != null) {
            colorCompleted = new CustomPlaceholderProgressBar.ConstantBarColor(TextColor.parse(this.colorCompleted.getValue(), tcc, this.colorCompleted.getStartMark()));
        }

        if (this.colorCompletedSteps != null) {
            if (this.colorCompleted != null) {
                tcc.getErrorHandler().addWarning("In !progress_bar you cannot use the colorCompleted and colorCompletedSteps at the same time. Those are mutually exclusive.", this.colorCompleted.getStartMark());
            }

            int[] colorCompletedSteps = new int[this.colorCompletedSteps.size()];
            TextColor[] colorCompletedColors = new TextColor[this.colorCompletedSteps.size()];
            val list = new ArrayList<>(this.colorCompletedSteps.entrySet());
            list.sort(Comparator.comparingInt(entry -> entry.getKey().getValue()));
            for (int i = 0; i < list.size(); i++) {
                val entry = list.get(i);
                colorCompletedSteps[i] = entry.getKey().getValue();
                colorCompletedColors[i] = TextColor.parse(entry.getValue().getValue(), tcc, entry.getValue().getStartMark());
            }

            if (this.colorCompletedInterpolateSteps == null || this.colorCompletedInterpolateSteps.isValue() == false) {
                colorCompleted = new CustomPlaceholderProgressBar.StepBarColor(colorCompletedSteps, colorCompletedColors);
            } else {
                colorCompleted = new CustomPlaceholderProgressBar.InterpolateBarColor(colorCompletedSteps, colorCompletedColors);
            }
        }

        TextColor colorCurrent = null;
        if (this.colorCurrent != null) {
            colorCurrent = TextColor.parse(this.colorCurrent.getValue(), tcc, this.colorCurrent.getStartMark());
        }

        boolean colorCurrentInterpolate = false;
        if (this.colorCurrentInterpolate != null) {
            colorCurrentInterpolate = this.colorCurrentInterpolate.isValue();
        }

        CustomPlaceholderProgressBar.BarColor colorRemaining = CustomPlaceholderProgressBar.BarColor.NONE;

        if (this.colorRemaining != null) {
            colorRemaining = new CustomPlaceholderProgressBar.ConstantBarColor(TextColor.parse(this.colorRemaining.getValue(), tcc, this.colorRemaining.getStartMark()));
        }

        if (this.colorRemainingSteps != null) {
            if (this.colorRemaining != null) {
                tcc.getErrorHandler().addWarning("In !progress_bar you cannot use the colorRemaining and colorRemainingSteps at the same time. Those are mutually exclusive.", this.colorRemaining.getStartMark());
            }

            int[] colorRemainingSteps = new int[this.colorRemainingSteps.size()];
            TextColor[] colorRemainingColors = new TextColor[this.colorRemainingSteps.size()];
            val list = new ArrayList<>(this.colorRemainingSteps.entrySet());
            list.sort(Comparator.comparingInt(entry -> entry.getKey().getValue()));
            for (int i = 0; i < list.size(); i++) {
                val entry = list.get(i);
                colorRemainingSteps[i] = entry.getKey().getValue();
                colorRemainingColors[i] = TextColor.parse(entry.getValue().getValue(), tcc, entry.getValue().getStartMark());
            }

            if (this.colorRemainingInterpolateSteps == null || this.colorRemainingInterpolateSteps.isValue() == false) {
                colorRemaining = new CustomPlaceholderProgressBar.StepBarColor(colorRemainingSteps, colorRemainingColors);
            } else {
                colorRemaining = new CustomPlaceholderProgressBar.InterpolateBarColor(colorRemainingSteps, colorRemainingColors);
            }
        }

        if (this.colorRemainingFromColorCompleted != null && colorRemainingFromColorCompleted.isValue()) {
            if (this.colorRemaining != null) {
                tcc.getErrorHandler().addWarning("In !progress_bar you cannot use the colorRemaining and colorRemainingFromColorCompleted at the same time. Those are mutually exclusive.", this.colorRemaining.getStartMark());
            }
            if (this.colorRemainingSteps != null) {
                tcc.getErrorHandler().addWarning("In !progress_bar you cannot use the colorRemainingSteps and colorRemainingFromColorCompleted at the same time. Those are mutually exclusive.", this.colorRemaining.getStartMark());
            }

            colorRemaining = new CustomPlaceholderProgressBar.DarkerBarColor(colorCompleted);
        }

        TextColor colorEmptyBar = null;
        if (this.colorEmptyBar != null) {
            colorEmptyBar = TextColor.parse(this.colorEmptyBar.getValue(), tcc, this.colorEmptyBar.getStartMark());
        }

        TextColor colorFullBar = null;
        if (this.colorFullBar != null) {
            colorFullBar = TextColor.parse(this.colorFullBar.getValue(), tcc, this.colorFullBar.getStartMark());
        }

        boolean emptyBarShowSymbols = true;
        if (this.emptyBarShowSymbols != null) {
            emptyBarShowSymbols = this.emptyBarShowSymbols.isValue();
        }

        boolean fullBarShowSymbols = true;
        if (this.fullBarShowSymbols != null) {
            fullBarShowSymbols = this.fullBarShowSymbols.isValue();
        }

        TextTemplate emptyBar = null;
        if (this.emptyBar != null) {
            emptyBar = TextTemplate.parse(this.emptyBar.getValue(), this.emptyBar.getStartMark(), barContext);
        }

        TextTemplate fullBar = null;
        if (this.fullBar != null) {
            fullBar = TextTemplate.parse(this.fullBar.getValue(), this.fullBar.getStartMark(), barContext);
        }

        ExpressionTemplate finalValue = value;
        ExpressionTemplate finalMinValue = minValue;
        ExpressionTemplate finalMaxValue = maxValue;
        int finalLength = length;
        String finalSymbolCompleted = symbolCompleted;
        String finalSymbolRemaining = symbolRemaining;
        List<String> finalSymbolsCurrent = symbolsCurrent;
        TextTemplate finalBorderLeft = borderLeft;
        TextTemplate finalBorderRight = borderRight;
        TextTemplate finalTextCenter = textCenter;
        TextTemplate finalTextCenterEmpty = textCenterEmpty;
        TextTemplate finalTextCenterFull = textCenterFull;
        CustomPlaceholderProgressBar.BarColor finalColorCompleted = colorCompleted;
        TextColor finalColorCurrent = colorCurrent;
        boolean finalColorCurrentInterpolate = colorCurrentInterpolate;
        CustomPlaceholderProgressBar.BarColor finalColorRemaining = colorRemaining;
        TextColor finalColorEmptyBar = colorEmptyBar;
        TextColor finalColorFullBar = colorFullBar;
        boolean finalEmptyBarShowSymbols = emptyBarShowSymbols;
        boolean finalFullBarShowSymbols = fullBarShowSymbols;
        TextTemplate finalEmptyBar = emptyBar;
        TextTemplate finalFullBar = fullBar;
        return builder.acquireData(() -> new CustomPlaceholderProgressBar(finalValue, finalMinValue, finalMaxValue, finalLength, finalSymbolCompleted, finalSymbolRemaining, finalSymbolsCurrent, finalBorderLeft, finalBorderRight, finalTextCenter, finalTextCenterEmpty, finalTextCenterFull, finalColorCompleted, finalColorCurrent, finalColorCurrentInterpolate, finalColorRemaining, finalColorEmptyBar, finalColorFullBar, finalEmptyBarShowSymbols, finalFullBarShowSymbols, finalEmptyBar, finalFullBar), TypeToken.STRING, true);
    }
}
