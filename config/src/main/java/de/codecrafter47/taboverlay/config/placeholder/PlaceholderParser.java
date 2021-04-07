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
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import org.yaml.snakeyaml.error.Mark;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParser {
    private static final Pattern PATTERN = Pattern.compile("(?ms)(\\$\\{|}|\\s)");

    private static Map<TypeToken<?>, Function<String, Function<?, String>>> dataRepresentations;

    static {
        dataRepresentations = new HashMap<>();
        dataRepresentations.put(TypeToken.INTEGER, arg -> {
            final String format = "%0" + arg + "d";
            //noinspection ResultOfMethodCallIgnored
            String.format(format, 1); // just test whether format string is correct
            return (Integer v) -> {
                if (v == null) {
                    return "";
                }
                return String.format(format, v);
            };
        });
        dataRepresentations.put(TypeToken.FLOAT, arg -> {
            final String format = "%0" + arg + "f";
            //noinspection ResultOfMethodCallIgnored
            String.format(format, 1.0f); // just test whether format string is correct
            return (Float v) -> {
                if (v == null) {
                    return "";
                }
                return String.format(format, v);
            };
        });
        dataRepresentations.put(TypeToken.DOUBLE, arg -> {
            final String format = "%0" + arg + "f";
            //noinspection ResultOfMethodCallIgnored
            String.format(format, 1.0f); // just test whether format string is correct
            return (Double v) -> {
                if (v == null) {
                    return "";
                }
                return String.format(format, v);
            };
        });
        dataRepresentations.put(TypeToken.STRING, arg -> {
            final int length = Integer.parseInt(arg);
            return (String v) -> {
                if (v == null) {
                    return "";
                }
                return v.length() > length ? v.substring(0, length) : (String) v;
            };
        });
    }

    /**
     * Parses a placeholder
     *
     * @param text     the text
     * @param position the position. Initially points to the first character after `${`. At the end points to the character after `$`.
     * @param mark     mark for error reporting
     * @param tcc      context
     * @return the placeholder
     */
    public static Placeholder parse(String text, ParsePosition position, Mark mark, TemplateCreationContext tcc) {

        List<PlaceholderArg> args = new ArrayList<>();
        List<PlaceholderArg> complexArgParts = new ArrayList<>();

        int placeholderStartPosition = position.getIndex() - 2;

        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find(position.getIndex())) {
            if (matcher.start() != position.getIndex()) {
                complexArgParts.add(new PlaceholderArg.Text(text.substring(position.getIndex(), matcher.start())));
            }
            position.setIndex(matcher.end());
            if ("${".equals(matcher.group())) {
                complexArgParts.add(new PlaceholderArg.Placeholder(parse(text, position, mark, tcc), text.substring(matcher.start(), position.getIndex())));
            } else {
                if (complexArgParts.size() == 1) {
                    args.add(complexArgParts.remove(0));
                } else if (complexArgParts.size() > 1) {
                    args.add(new PlaceholderArg.Complex(complexArgParts));
                    complexArgParts = new ArrayList<>();
                }
            }
            if ("}".equals(matcher.group())) {
                String placeholderText = text.substring(placeholderStartPosition, position.getIndex());
                try {
                    tcc.getErrorHandler().enterContext("in use of placeholder " + placeholderText, mark);
                    try {
                        PlaceholderBuilder<?, ?> builder = tcc.getPlaceholderResolverChain().resolve(PlaceholderBuilder.create(), args, tcc);
                        if (!args.isEmpty()) {
                            if (args.size() == 1 && args.get(0) instanceof PlaceholderArg.Text) {
                                String format = args.get(0).getText();
                                builder = applyFormat(builder, format);
                            } else if (args.size() > 1) {
                                tcc.getErrorHandler().addWarning("Parts of the placeholder text have been ignored.", null);
                            } else {
                                tcc.getErrorHandler().addWarning("Use of placeholders in format string is not allowed.", null);
                            }
                        }
                        return builder.build();
                    } finally {
                        tcc.getErrorHandler().leaveContext();
                    }
                } catch (UnknownPlaceholderException e) {
                    tcc.getErrorHandler().addWarning("Unknown placeholder " + placeholderText, mark);
                } catch (PlaceholderException e) {
                    String message = "Error in placeholder " + placeholderText + ":\n" + e.getMessage();
                    if (e.getCause() != null) {
                        message = message + "\nCaused by: " + e.getCause().getMessage();
                    }
                    tcc.getErrorHandler().addWarning(message, mark);
                }
                return Placeholder.DUMMY;
            }
        }

        tcc.getErrorHandler().addWarning("Encountered missing `}` while parsing placeholder.", mark);
        return Placeholder.DUMMY;
    }

    @SuppressWarnings("unchecked")
    private static PlaceholderBuilder<?, ?> applyFormat(PlaceholderBuilder<?, ?> builder, String format) {
        Function<String, Function<?, String>> representationFunction = dataRepresentations.get(builder.getType());
        if (representationFunction != null) {
            builder = builder.transformData((Function) representationFunction.apply(format), TypeToken.STRING);
        }
        return builder;
    }
}
