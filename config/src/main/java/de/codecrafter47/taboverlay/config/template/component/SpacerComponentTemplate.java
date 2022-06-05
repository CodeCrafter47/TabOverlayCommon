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

package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.SpacerComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpacerComponentTemplate implements ComponentTemplate {
    TextTemplate defaultText;
    PingTemplate defaultPing;
    IconTemplate defaultIcon;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(false)
                .minSize(0)
                .blockAligned(false)
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new SpacerComponentView(defaultText.instantiate(), defaultPing.instantiate(), defaultIcon.instantiate());
    }
}
