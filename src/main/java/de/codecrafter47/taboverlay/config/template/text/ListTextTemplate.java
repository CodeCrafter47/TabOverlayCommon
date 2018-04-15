package de.codecrafter47.taboverlay.config.template.text;

import de.codecrafter47.taboverlay.config.view.text.TextViewList;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListTextTemplate implements TextTemplate {
    private final List<TextTemplate> templates;

    public ListTextTemplate(List<TextTemplate> templates) {
        this.templates = templates;
    }

    @Override
    @Nonnull
    @NonNull
    public TextView instantiate() {
        ArrayList<TextView> list = new ArrayList<>(templates.size());
        for (TextTemplate template : templates) {
            list.add(Objects.requireNonNull(template.instantiate()));
        }
        return new TextViewList(list);
    }
}
