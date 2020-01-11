package de.jaggl.sqlbuilder.springjdbc.builders;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import de.jaggl.sqlbuilder.columns.Column;
import de.jaggl.sqlbuilder.schema.Table;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PACKAGE)
public class InsertFromTableBuilder
{
    private final Table table;
    private final DataSource dataSource;

    private List<Column> excludedColumns;
    private List<Column> includedColumns;

    private List<Column> settingAutoIncrementColumns;

    public SimpleJdbcInsert build()
    {
        var insert = new SimpleJdbcInsert(dataSource).withTableName(table.getName()).usingColumns(getColumnNames());
        if (table.getSchema() != null)
        {
            insert.withSchemaName(table.getSchema().getName());
        }
        String[] autoIncrementedColumns = getAutoIncrementableColumnNames();
        if (autoIncrementedColumns.length > 0)
        {
            insert.usingGeneratedKeyColumns(autoIncrementedColumns);
        }
        return insert;
    }

    public SimpleJdbcInsert buildAndCompile()
    {
        var insert = build();
        insert.compile();
        return insert;
    }

    public InsertFromTableBuilder withColumns(Column... columns)
    {
        includedColumns = new ArrayList<>(List.of(columns));
        excludedColumns = null;
        return this;
    }

    public InsertFromTableBuilder withoutColumns(Column... columns)
    {
        excludedColumns = new ArrayList<>(List.of(columns));
        includedColumns = null;
        return this;
    }

    public InsertFromTableBuilder withSettingGeneratedKeyColumns(Column... columns)
    {
        settingAutoIncrementColumns = new ArrayList<>(List.of(columns));
        return this;
    }

    private String[] getAutoIncrementableColumnNames()
    {
        return table.getColumns()
                .stream()
                .filter(this::isAutoIncrement)
                .map(Column::getName)
                .collect(toList())
                .toArray(new String[0]);
    }

    private String[] getColumnNames()
    {
        return table.getColumns()
                .stream()
                .filter(this::isRelevantColumn)
                .map(Column::getName)
                .collect(toList())
                .toArray(new String[0]);
    }

    private boolean isRelevantColumn(Column column)
    {
        var result = true;
        if (includedColumns != null)
        {
            result = includedColumns.stream().anyMatch(col -> col == column);
        }
        else if (excludedColumns != null)
        {
            result = excludedColumns.stream().noneMatch(col -> col == column);
        }
        if (column.getColumnDefinition().isAutoIncrement() && settingAutoIncrementColumns != null)
        {
            return settingAUtoIncrementColumnsContainColumn(column);
        }
        return result;
    }

    private boolean settingAUtoIncrementColumnsContainColumn(Column column)
    {
        return settingAutoIncrementColumns.stream().anyMatch(col -> col == column);
    }

    private boolean isAutoIncrement(Column column)
    {
        return column.getColumnDefinition().isAutoIncrement() && (settingAutoIncrementColumns == null || settingAutoIncrementColumns.stream()
                .noneMatch(col -> col == column));
    }
}
