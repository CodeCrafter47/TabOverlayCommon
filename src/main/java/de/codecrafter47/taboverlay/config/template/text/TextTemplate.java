package de.codecrafter47.taboverlay.config.template.text;

import de.codecrafter47.taboverlay.config.placeholder.PlaceholderParser;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;
import org.yaml.snakeyaml.error.Mark;

import javax.annotation.Nonnull;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TextTemplate {

    TextTemplate EMPTY = new ConstantTextTemplate("");
    Pattern PATTERN_PLACEHOLDER_START = Pattern.compile("(?ms)\\$\\{");

    static TextTemplate parse(String text, Mark mark, TemplateCreationContext tcc) {
        List<TextTemplate> templates = new ArrayList<>();
        Matcher matcher = PATTERN_PLACEHOLDER_START.matcher(text);
        ParsePosition position = new ParsePosition(0);
        while (matcher.find(position.getIndex())) {
            templates.add(new ConstantTextTemplate(text.substring(position.getIndex(), matcher.start())));
            position.setIndex(matcher.end());
            templates.add(new PlaceholderTextTemplate(PlaceholderParser.parse(text, position, mark, tcc)));
        }
        templates.add(new ConstantTextTemplate(text.substring(position.getIndex())));
        if (templates.size() == 1) {
            return templates.get(0);
        } else {
            return new ListTextTemplate(templates);
        }
    }

    @Nonnull
    @NonNull
    TextView instantiate();
}
