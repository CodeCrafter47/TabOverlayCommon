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
                .size(size)
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
    }
}
