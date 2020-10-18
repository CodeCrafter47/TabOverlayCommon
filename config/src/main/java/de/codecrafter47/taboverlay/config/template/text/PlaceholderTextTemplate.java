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

import de.codecrafter47.taboverlay.config.placeholder.Placeholder;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A placeholder text template.
 */
public class PlaceholderTextTemplate implements TextTemplate {

    private final Placeholder placeholder;

    public PlaceholderTextTemplate(Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    @Nonnull
    @NonNull
    public TextView instantiate() {
        return Objects.requireNonNull(placeholder.instantiate());
    }

    @Override
    public boolean requiresViewerContext() {
        return placeholder.requiresViewerContext();
    }
}
