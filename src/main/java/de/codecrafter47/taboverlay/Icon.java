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

package de.codecrafter47.taboverlay;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;

@Value
public class Icon implements Serializable {
    private static final long serialVersionUID = -8251683111229590559L;

    public static final Icon DEFAULT_STEVE = new Icon(null, true, false);
    public static final Icon DEFAULT_ALEX = new Icon(null, false, true);

    private final ProfileProperty textureProperty;
    private final boolean steve;
    private final boolean alex;

    private Icon(ProfileProperty textureProperty, boolean steve, boolean alex) {
        this.textureProperty = textureProperty;
        this.steve = steve;
        this.alex = alex;
    }

    public Icon(@Nonnull @NonNull ProfileProperty textureProperty) {
        this(textureProperty, false, false);
    }

    public boolean hasTextureProperty() {
        return textureProperty != null;
    }
}
