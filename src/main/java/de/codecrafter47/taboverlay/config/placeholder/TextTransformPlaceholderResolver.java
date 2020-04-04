package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TextTransformPlaceholderResolver extends AbstractPlaceholderResolver<Context> {

    public TextTransformPlaceholderResolver() {
        addPlaceholder("uppercase", create(String::toUpperCase));
        addPlaceholder("lowercase", create(String::toLowerCase));
        addPlaceholder("small_caps", create(CharacterMapping.from("-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ")));
        addPlaceholder("subscript", create(CharacterMapping.from("₋₀₁₂₃₄₅₆₇₈₉ₐBCDₑFGₕᵢⱼₖₗₘₙₒₚQᵣₛₜᵤᵥWₓYZₐᵦcdₑfgₕᵢⱼₖₗₘₙₒₚᵩᵣₛₜᵤᵥwₓᵧz")));
        addPlaceholder("superscript", create(CharacterMapping.from("⁻⁰¹²³⁴⁵⁶⁷⁸⁹ᴬᴮᶜᴰᴱᶠᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾᵠᴿˢᵀᵁⱽᵂˣʸᶻᵃᵇᶜᵈᵉᶠᵍʰᶦʲᵏˡᵐⁿᵒᵖᵠʳˢᵗᵘᵛʷˣʸᶻ")));
        addPlaceholder("bubbles", create(CharacterMapping.from("-⓪①②③④⑤⑥⑦⑧⑨ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ")));
    }

    private PlaceholderResolver<Context> create(Function<String, String> transform) {
        return new PlaceholderResolver<Context>() {
            @Nonnull
            @Override
            public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
                List<ExpressionTemplate> parts = new ArrayList<>();
                for (PlaceholderArg arg : args) {
                    parts.add(arg.getExpression());
                    parts.add(ConstantExpressionTemplate.of(" "));
                }
                parts.remove(parts.size() - 1);
                ExpressionTemplate text = ExpressionTemplates.concat(parts);

                args.clear();
                return builder.acquireData(() -> new TextTransformPlaceholder(text, transform), TypeToken.STRING, text.requiresViewerContext());
            }
        };
    }

    public static class TextTransformPlaceholder extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener {

        private ToStringExpression expression;
        private Function<String, String> transform;
        private String text;

        public TextTransformPlaceholder(ExpressionTemplate textTemplate, Function<String, String> transform) {
            this.expression = textTemplate.instantiateWithStringResult();
            this.transform = transform;
        }

        @Override
        public String getData() {
            return text;
        }

        @Override
        protected void onActivation() {
            expression.activate(getContext(), this);
            text = transform.apply(expression.evaluate());
        }

        @Override
        protected void onDeactivation() {
            expression.deactivate();
        }

        @Override
        public void onExpressionUpdate() {
            text = transform.apply(expression.evaluate());
            if (hasListener()) {
                getListener().run();
            }
        }
    }

    private static class CharacterMapping implements Function<String, String> {
        private static final char[] keys = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        private final char[] values;

        private CharacterMapping(char[] values) {
            this.values = values;
        }

        private static CharacterMapping from(String values) {
            char[] chars = values.toCharArray();
            if (chars.length != keys.length) {
                throw new AssertionError();
            }
            return new CharacterMapping(chars);
        }

        @Override
        public String apply(String s) {
            StringBuilder sb = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                int j = Arrays.binarySearch(keys, c);
                sb.append(j >= 0 ? values[j] : c);
            }
            return sb.toString();
        }
    }
}
