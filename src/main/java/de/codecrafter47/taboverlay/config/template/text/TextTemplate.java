package de.codecrafter47.taboverlay.config.template.text;

import de.codecrafter47.taboverlay.config.placeholder.PlaceholderException;
import de.codecrafter47.taboverlay.config.placeholder.UnknownPlaceholderException;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;
import org.yaml.snakeyaml.error.Mark;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TextTemplate {

    TextTemplate EMPTY = new ConstantTextTemplate("");
    Pattern PATTERN_PLACEHOLDER = Pattern.compile("(?ms)\\$\\{([^}]+)\\}");

    static TextTemplate parse(String text, Mark mark, TemplateCreationContext tcc) {
        List<TextTemplate> templates = new ArrayList<>();
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(text);
        while (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            matcher.appendReplacement(sb, "");
            templates.add(new ConstantTextTemplate(sb.toString()));
            try {
                templates.add(new PlaceholderTextTemplate(tcc.getPlaceholderResolverChain().resolve(matcher.group(1).split(" "), tcc)));
            } catch (UnknownPlaceholderException e) {
                tcc.getErrorHandler().addWarning("Unknown placeholder " + matcher.group(), mark);
            } catch (PlaceholderException e) {
                String message = "Error in placeholder " + matcher.group() + ":\n" + e.getMessage();
                if (e.getCause() != null) {
                    message = message + "\nCaused by: " + e.getCause().getMessage();
                }
                tcc.getErrorHandler().addWarning(message, mark);
            }
        }
        StringBuffer sb = new StringBuffer();
        matcher.appendTail(sb);
        templates.add(new ConstantTextTemplate(sb.toString()));
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
