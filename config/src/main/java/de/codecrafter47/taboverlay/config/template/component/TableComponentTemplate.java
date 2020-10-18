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
import de.codecrafter47.taboverlay.config.view.components.TableComponentView;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class TableComponentTemplate implements ComponentTemplate {
    private List<Column> columns;
    /* A value of -1 indicates dynamic size */
    private int size;
    private int columnCount;
    TextTemplate defaultText;
    PingTemplate defaultPing;
    IconTemplate defaultIcon;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(size != -1)
                .minSize(size != -1
                        ? size
                        : columns.stream().mapToInt(Column::getMinSize).max().orElse(0))
                .blockAligned(true)
                .build();
    }

    @Override
    public ComponentView instantiate() {

        val columns = new ArrayList<TableComponentView.Column>(this.columns.size());

        for (Column column : this.columns) {
            columns.add(column.instantiate());
        }

        return new TableComponentView(columns,
                size,
                columnCount,
                defaultText.instantiate(),
                defaultPing.instantiate(),
                defaultIcon.instantiate());
    }

    @Value
    @Builder
    public static class Column {
        private int columnIndex;
        private int width;
        private ComponentTemplate component;

        private TableComponentView.Column instantiate() {
            return new TableComponentView.Column(columnIndex, width, component.instantiate());
        }

        private int getMinSize() {
            return component.getLayoutInfo().getMinSize();
        }
    }
}
