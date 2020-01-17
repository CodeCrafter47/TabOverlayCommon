package de.codecrafter47.taboverlay.config.dsl.components;

import com.google.common.collect.ImmutableList;
import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedMapProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.TableComponentTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.OptionalInt;

@Getter
@Setter
public class TableComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {
    private MarkedIntegerProperty size = new MarkedIntegerProperty(-1);
    private MarkedMapProperty<MarkedStringProperty, ComponentConfiguration> columns = new MarkedMapProperty<>();

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {

        OptionalInt columnsCount = tcc.getColumns();
        if (!columnsCount.isPresent()) {
            tcc.getErrorHandler().addError("table component cannot be used in dynamic size tab lists", getStartMark());
            return tcc.emptyComponent(); // abort with dummy object
        }

        if (size == null) {
            size = new MarkedIntegerProperty(-1);
        }
        if (size.getValue() < -1) {
            tcc.getErrorHandler().addError("Failed to configure table component. Size is negative", size.getStartMark());
        } else if (size.getValue() != -1 && size.getValue() % columnsCount.getAsInt() != 0) {
            tcc.getErrorHandler().addError("Failed to configure table component. Size is not a multiple of " + columnsCount.getAsInt(), size.getStartMark());
        }

        val columnList = ImmutableList.<TableComponentTemplate.Column>builder();
        if (ConfigValidationUtil.checkNotNull(tcc, "!table component", "columns", columns, getStartMark())) {
            int nextAvailableColumnIndex = 0;

            for (val entry : this.columns.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                val key = entry.getKey().getValue();
                int columnIndex, width;
                try {
                    if (key.contains("-")) {
                        String[] tokens = key.split("-");
                        columnIndex = Integer.valueOf(tokens[0]);
                        width = Integer.valueOf(tokens[1]) - columnIndex + 1;
                    } else {
                        columnIndex = Integer.valueOf(key);
                        width = 1;
                    }

                    if (columnIndex < nextAvailableColumnIndex) {
                        tcc.getErrorHandler().addError("Failed to configure table component. Columns not ordered", entry.getKey().getStartMark());
                    } else if (width < 1) {
                        tcc.getErrorHandler().addError("Failed to configure table component. Column has illegal width", entry.getKey().getStartMark());
                    } else if (columnIndex + width > columnsCount.getAsInt()) {
                        tcc.getErrorHandler().addError("Failed to configure table component. Column outside the tab overlay (available columns here: 0-" + columnsCount.getAsInt() + ").", entry.getKey().getStartMark());
                    } else {
                        val component = entry.getValue();
                        TemplateCreationContext childContext = tcc.clone();
                        childContext.setColumns(width);
                        columnList.add(TableComponentTemplate.Column.builder()
                                .columnIndex(columnIndex)
                                .width(width)
                                .component(component != null ? component.toTemplate(childContext) : tcc.emptyComponent())
                                .build());
                    }
                } catch (NumberFormatException e) {
                    tcc.getErrorHandler().addError("Failed to configure table component. Failed to parse column key: " + e.getMessage(), entry.getKey().getStartMark());
                }
            }
        }

        return TableComponentTemplate.builder()
                .size(size.getValue())
                .columns(columnList.build())
                .columnCount(columnsCount.getAsInt())
                .defaultIcon(tcc.getDefaultIcon())
                .defaultText(tcc.getDefaultText())
                .defaultPing(tcc.getDefaultPing())
                .build();
    }
}
