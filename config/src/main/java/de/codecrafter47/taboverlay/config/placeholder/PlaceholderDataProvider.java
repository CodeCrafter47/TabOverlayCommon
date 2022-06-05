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

package de.codecrafter47.taboverlay.config.placeholder;

import java.util.function.Function;

public interface PlaceholderDataProvider<C, D> {

    void activate(C context, Runnable listener);

    void deactivate();

    D getData();

    default <D2> PlaceholderDataProvider<C, D2> transformData(Function<D, D2> transormation) {
        return new PlaceholderDataProvider<C, D2>() {
            @Override
            public void activate(C context, Runnable listener) {
                PlaceholderDataProvider.this.activate(context, listener);
            }

            @Override
            public void deactivate() {
                PlaceholderDataProvider.this.deactivate();
            }

            @Override
            public D2 getData() {
                return transormation.apply(PlaceholderDataProvider.this.getData());
            }
        };
    }
}
