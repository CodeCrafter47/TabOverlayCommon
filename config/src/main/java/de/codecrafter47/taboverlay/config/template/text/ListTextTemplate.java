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

package de.codecrafter47.taboverlay.config.template.text;

import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewList;
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

    @Override
    public boolean requiresViewerContext() {
        return templates.stream().anyMatch(TextTemplate::requiresViewerContext);
    }
}
