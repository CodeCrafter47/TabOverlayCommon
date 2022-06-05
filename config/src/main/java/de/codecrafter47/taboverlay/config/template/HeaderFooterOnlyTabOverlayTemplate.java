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

package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.handler.ContentOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeaderFooterOnlyTabOverlayTemplate extends AbstractTabOverlayTemplate {

    @Override
    public AbstractActiveElement<?> createContentView(TabView tabView, TabOverlayHandler handler) {
        handler.enterContentOperationMode(ContentOperationMode.PASS_TROUGH);
        return new AbstractActiveElement<Object>() {
            @Override
            protected void onActivation() {
                // dummy
            }

            @Override
            protected void onDeactivation() {
                // dummy
            }
        };
    }
}
