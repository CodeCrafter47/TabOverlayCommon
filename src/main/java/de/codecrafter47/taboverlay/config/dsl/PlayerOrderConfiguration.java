package de.codecrafter47.taboverlay.config.dsl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.SortingRulePreprocessor;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.placeholder.UnknownPlaceholderException;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class PlayerOrderConfiguration extends MarkedPropertyBase {
    public static final PlayerOrderConfiguration DEFAULT = new PlayerOrderConfiguration("name as text asc");

    private static final Set<TypeToken<?>> NUMERIC_TYPES = ImmutableSet.of(TypeToken.INTEGER, TypeToken.FLOAT, TypeToken.DOUBLE);
    private static final Set<TypeToken<?>> STRING_TYPES = ImmutableSet.of(TypeToken.STRING);

    private static final Map<String, PlayerOrderTemplate.Direction> DIRECTION_ID_MAP = ImmutableMap.<String, PlayerOrderTemplate.Direction>builder()
            .put("ascending", PlayerOrderTemplate.Direction.ASCENDING)
            .put("asc", PlayerOrderTemplate.Direction.ASCENDING)
            .put("descending", PlayerOrderTemplate.Direction.DESCENDING)
            .put("desc", PlayerOrderTemplate.Direction.DESCENDING)
            .put("viewer-first", PlayerOrderTemplate.Direction.VIEWER_FIRST)
            .build();

    private static final Map<String, PlayerOrderTemplate.Type> TYPE_ID_MAP = ImmutableMap.<String, PlayerOrderTemplate.Type>builder()
            .put("number", PlayerOrderTemplate.Type.NUMBER)
            .put("text", PlayerOrderTemplate.Type.TEXT)
            .put("string", PlayerOrderTemplate.Type.TEXT)
            .build();

    private final String order;

    public PlayerOrderTemplate toTemplate(TemplateCreationContext tcc) {

        SortingRulePreprocessor preprocessor = tcc.getSortingRulePreprocessor();

        List<PlayerOrderTemplate.Entry> chain = new ArrayList<>();

        if (order != null) {
            String[] elements = order.split(",");
            for (String element : elements) {

                if (preprocessor != null) {
                    element = preprocessor.process(element, tcc.getErrorHandler(), getStartMark());
                }

                String[] tokens = element.trim().split(" ");

                if (tokens.length == 0) {
                    tcc.getErrorHandler().addWarning("Player Order contains empty entry. Too many `,`'s?", getStartMark());
                    continue;
                }

                String placeholderId = tokens[0];

                PlayerPlaceholder<?, ?> placeholder;
                try {
                    placeholder = tcc.getPlayerPlaceholderResolver().resolve(PlayerPlaceholder.BindPoint.VIEWER, new String[]{placeholderId});
                } catch (UnknownPlaceholderException e) {
                    tcc.getErrorHandler().addWarning("Unknown placeholder in playerOrder option: `" + placeholderId + "`", getStartMark());
                    continue;
                }

                PlayerOrderTemplate.Direction direction = null;
                PlayerOrderTemplate.Type type = null;

                for (int i = 1; i < tokens.length; i++) {
                    String token = tokens[i];

                    if (DIRECTION_ID_MAP.containsKey(token)) {
                        // it's a direction-id
                        if (direction != null) {
                            tcc.getErrorHandler().addWarning("In playerOrder: Ignoring option `" + token + "` for `" + placeholderId + "` because direction has already been set.", getStartMark());
                            continue;
                        }
                        direction = DIRECTION_ID_MAP.get(token);
                    } else if (token.equals("as")) {
                        if (++i == tokens.length) {
                            tcc.getErrorHandler().addWarning("In playerOrder: After `" + placeholderId + "` encountered `as` needs to be followed by `text` or `number`.", getStartMark());
                            continue;
                        }
                        token = tokens[i];
                        if (!TYPE_ID_MAP.containsKey(token)) {
                            tcc.getErrorHandler().addWarning("In playerOrder: After `" + placeholderId + "` encountered unknown type: `as " + token + "`. Try using `as text` or `as number` instead.", getStartMark());
                            continue;
                        }
                        if (type != null) {
                            tcc.getErrorHandler().addWarning("In playerOrder: Ignoring option `as " + token + "` for `" + placeholderId + "` because type has already been set.", getStartMark());
                            continue;
                        }
                        type = TYPE_ID_MAP.get(token);
                    } else {
                        tcc.getErrorHandler().addWarning("In playerOrder: Ignoring option `" + token + "` for `" + placeholderId + "`. Unknown option.", getStartMark());
                        continue;
                    }
                    // todo other options
                }

                if (type == null) {
                    // defaults
                    TypeToken<?> placeholderType = placeholder.getType();
                    if (STRING_TYPES.contains(placeholderType)) {
                        type = PlayerOrderTemplate.Type.TEXT;
                    }
                    if (NUMERIC_TYPES.contains(placeholderType)) {
                        type = PlayerOrderTemplate.Type.NUMBER;
                    }
                }

                if (type == null) {
                    tcc.getErrorHandler().addWarning("In playerOrder: Missing type for `" + placeholderId + "`. Try `" + placeholderId + " as text` or `" + placeholderId + " as number` instead.", getStartMark());
                    continue;
                }

                if (direction == null) {
                    // defaults
                    if (type == PlayerOrderTemplate.Type.TEXT) {
                        direction = PlayerOrderTemplate.Direction.ASCENDING;
                    }
                }

                if (direction == null) {
                    tcc.getErrorHandler().addWarning("In playerOrder: Missing direction for `" + placeholderId + "`. Try `" + placeholderId + " asc` or `" + placeholderId + " desc` instead.", getStartMark());
                    continue;
                }

                chain.add(new PlayerOrderTemplate.Entry(placeholder, direction, type));
            }
        }

        return new PlayerOrderTemplate(Collections.unmodifiableList(chain));
    }
}
