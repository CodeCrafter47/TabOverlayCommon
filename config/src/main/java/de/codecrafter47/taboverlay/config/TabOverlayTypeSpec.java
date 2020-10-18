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

package de.codecrafter47.taboverlay.config;

import de.codecrafter47.taboverlay.config.dsl.AbstractTabOverlayTemplateConfiguration;
import lombok.Value;

@Value
public class TabOverlayTypeSpec {
    /**
     * Id to use for the type option of the config.
     */
    private String id;

    /**
     * Class to use for config deserialization.
     */
    private Class<? extends AbstractTabOverlayTemplateConfiguration> configurationClass;
}
