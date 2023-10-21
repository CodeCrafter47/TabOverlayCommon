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
        addPlaceholder("small_caps", create(CharacterMapping.from("-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280s\u1d1b\u1d1c\u1d20\u1d21x\u028f\u1d22")));
        addPlaceholder("subscript", create(CharacterMapping.from("\u208b\u2080\u2081\u2082\u2083\u2084\u2085\u2086\u2087\u2088\u2089\u2090BCD\u2091FG\u2095\u1d62\u2c7c\u2096\u2097\u2098\u2099\u2092\u209aQ\u1d63\u209b\u209c\u1d64\u1d65W\u2093YZ\u2090\u1d66cd\u2091fg\u2095\u1d62\u2c7c\u2096\u2097\u2098\u2099\u2092\u209a\u1d69\u1d63\u209b\u209c\u1d64\u1d65w\u2093\u1d67z")));
        addPlaceholder("superscript", create(CharacterMapping.from("\u207b\u2070\u00b9\u00b2\u00b3\u2074\u2075\u2076\u2077\u2078\u2079\u1d2c\u1d2e\u1d9c\u1d30\u1d31\u1da0\u1d33\u1d34\u1d35\u1d36\u1d37\u1d38\u1d39\u1d3a\u1d3c\u1d3e\u1d60\u1d3f\u02e2\u1d40\u1d41\u2c7d\u1d42\u02e3\u02b8\u1dbb\u1d43\u1d47\u1d9c\u1d48\u1d49\u1da0\u1d4d\u02b0\u1da6\u02b2\u1d4f\u02e1\u1d50\u207f\u1d52\u1d56\u1d60\u02b3\u02e2\u1d57\u1d58\u1d5b\u02b7\u02e3\u02b8\u1dbb")));
        addPlaceholder("bubbles", create(CharacterMapping.from("-\u24ea\u2460\u2461\u2462\u2463\u2464\u2465\u2466\u2467\u2468\u24b6\u24b7\u24b8\u24b9\u24ba\u24bb\u24bc\u24bd\u24be\u24bf\u24c0\u24c1\u24c2\u24c3\u24c4\u24c5\u24c6\u24c7\u24c8\u24c9\u24ca\u24cb\u24cc\u24cd\u24ce\u24cf\u24d0\u24d1\u24d2\u24d3\u24d4\u24d5\u24d6\u24d7\u24d8\u24d9\u24da\u24db\u24dc\u24dd\u24de\u24df\u24e0\u24e1\u24e2\u24e3\u24e4\u24e5\u24e6\u24e7\u24e8\u24e9")));
        addPlaceholder("rainbow", create(TextTransformPlaceholderResolver::rainbowString));
        addPlaceholder("capitalize", create(TextTransformPlaceholderResolver::capitalize));
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

    private static String rainbowString(String str) {
        String rainbowColors = "c6eabd";
        StringBuilder sb = new StringBuilder(3 * str.length());
        int colorIndex = 0;

        for (int i = 0; i < str.length(); ++i) {
            sb.append('&');
            sb.append(rainbowColors.charAt(colorIndex % rainbowColors.length()));
            sb.append(str.charAt(i));
            if (str.charAt(i) != 32) {
                ++colorIndex;
            }
        }

        return sb.toString();
    }

    private static String capitalize(String str) {
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
