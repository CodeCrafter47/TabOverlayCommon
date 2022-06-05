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

package de.codecrafter47.taboverlay.config.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import org.yaml.snakeyaml.error.Mark;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public interface IconManager {

    IconTemplate createIconTemplate(String s, Mark mark, ErrorHandler errorHandler);

    CompletableFuture<Icon> createIcon(BufferedImage image);

    CompletableFuture<Icon> createIconFromName(String name);
}
