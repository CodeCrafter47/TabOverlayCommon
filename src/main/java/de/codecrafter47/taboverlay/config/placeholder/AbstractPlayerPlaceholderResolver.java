package de.codecrafter47.taboverlay.config.placeholder;

import com.google.common.base.Strings;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.util.Unchecked;
import lombok.val;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractPlayerPlaceholderResolver {
    private Map<TypeToken<?>, Function<String[], Function<?, String>>> dataRepresentations = new HashMap<>();
    private Map<String, BiFunction<PlayerPlaceholder.BindPoint, String[], PlayerPlaceholder<?, ?>>> placeholders = new HashMap<>();

    public AbstractPlayerPlaceholderResolver() {
        addPlaceholder("name", create(TypeToken.STRING, null, Player::getName, i -> i));
        addPlaceholder("uuid", create(TypeToken.STRING, null, player -> player.getUniqueID().toString(), i -> i));
        addRepresentation(TypeToken.INTEGER, tokens -> {
            if (tokens.length > 0) {
                String token = tokens[0];
                final int length = Integer.valueOf(token);
                return (Integer v) -> {
                    if (v == null) {
                        return "";
                    }
                    String s = Integer.toString(v);
                    return Strings.padStart(s, length, ' ');
                };
            } else {
                return null;
            }
        });
        addRepresentation(TypeToken.FLOAT, tokens -> {
            if (tokens.length > 0) {
                String token = tokens[0];
                final String format = "%0" + token + "f";
                String.format(format, 1.0f); // just test whether format string is correct
                return (Float v) -> {
                    if (v == null) {
                        return "";
                    }
                    return String.format(format, v);
                };
            } else {
                return null;
            }
        });
        addRepresentation(TypeToken.DOUBLE, tokens -> {
            if (tokens.length > 0) {
                String token = tokens[0];
                final String format = "%0" + token + "f";
                String.format(format, 1.0f); // just test whether format string is correct
                return (Double v) -> {
                    if (v == null) {
                        return "";
                    }
                    return String.format(format, v);
                };
            } else {
                return null;
            }
        });
        addRepresentation(TypeToken.STRING, tokens -> {
            if (tokens.length > 0) {
                String token = tokens[0];
                final int length = Integer.valueOf(token);
                return (String v) -> {
                    if (v == null) {
                        return "";
                    }
                    return v.length() > length ? v.substring(0, length) : (String) v;
                };
            } else {
                return null;
            }
        });
    }

    public PlayerPlaceholder<?, ?> resolve(PlayerPlaceholder.BindPoint bindPoint, String[] tokens) throws UnknownPlaceholderException {
        String token;
        if (tokens.length == 0) {
            token = "name";
        } else {
            token = tokens[0];
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }

        val placeholderResolutionFunction = placeholders.get(token);
        if (placeholderResolutionFunction == null) {
            throw new UnknownPlaceholderException();
        }

        return placeholderResolutionFunction.apply(bindPoint, tokens);
    }

    protected final <T> Function<T, String> getRepresentationFunction(TypeToken<T> type, String[] tokens, Function<T, String> defaultRepresentation) {
        if (defaultRepresentation == null) {
            defaultRepresentation = o -> o != null ? String.valueOf(o) : "";
        }
        Function<String[], Function<T, String>> representationFunctionFactory = Unchecked.cast(dataRepresentations.get(type));
        if (representationFunctionFactory == null) {
            return defaultRepresentation;
        } else {
            Function<T, String> function = representationFunctionFactory.apply(tokens);
            return null != function ? function : defaultRepresentation;
        }
    }

    protected <T> BiFunction<PlayerPlaceholder.BindPoint, String[], PlayerPlaceholder<T, T>> create(DataKey<T> dataKey) {
        return create(dataKey, null, null);
    }

    protected <T> BiFunction<PlayerPlaceholder.BindPoint, String[], PlayerPlaceholder<T, T>> create(DataKey<T> dataKey, Function<Player, T> defaultValueFunction, Function<T, String> defaultRepresentation) {
        return create(dataKey.getType(), dataKey, defaultValueFunction, defaultRepresentation);
    }

    protected <T> BiFunction<PlayerPlaceholder.BindPoint, String[], PlayerPlaceholder<T, T>> create(TypeToken<T> type, DataKey<T> dataKey, Function<Player, T> defaultValueFunction, Function<T, String> defaultRepresentation) {
        return create(type, dataKey, null, defaultValueFunction, defaultRepresentation);
    }

    protected <R, T> BiFunction<PlayerPlaceholder.BindPoint, String[], PlayerPlaceholder<R, T>> create(TypeToken<T> type, DataKey<R> dataKey, Function<R, T> transformation, Function<Player, T> defaultValueFunction, Function<T, String> defaultRepresentation) {
        return (bindPoint, tokens) -> new PlayerPlaceholder<>(bindPoint, type, dataKey, transformation, defaultValueFunction, getRepresentationFunction(type, tokens, defaultRepresentation));
    }

    protected final <T> void addRepresentation(TypeToken<T> type, Function<String[], Function<T, String>> representationFunctionFactory) {
        dataRepresentations.put(type, Unchecked.cast(representationFunctionFactory));
    }

    protected final <R, T> void addPlaceholder(String name, BiFunction<PlayerPlaceholder.BindPoint, String[], PlayerPlaceholder<R, T>> function) {
        placeholders.put(name, Unchecked.cast(function));
    }

}
