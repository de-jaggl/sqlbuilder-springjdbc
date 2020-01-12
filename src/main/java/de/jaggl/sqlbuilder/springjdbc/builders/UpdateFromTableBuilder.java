package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.domain.Placeholder.placeholder;
import static de.jaggl.sqlbuilder.queries.Queries.update;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import de.jaggl.sqlbuilder.columns.Column;
import de.jaggl.sqlbuilder.conditions.Condition;
import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.domain.Placeholder;
import de.jaggl.sqlbuilder.schema.Table;
import lombok.RequiredArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor(access = PACKAGE)
public class UpdateFromTableBuilder
{
    private final Table table;
    private final DataSource dataSource;
    private final Dialect dialect;

    private List<Column> includedColumns;
    private List<Column> excludedColumns;

    private List<Column> updateAutoIncrementColumns;

    private Condition condition;

    private boolean usePlaceholderNames = true;

    public SqlUpdate build()
    {
        var parameterTypes = new ArrayList<Integer>();
        var sqlUpdate = new SqlUpdate(dataSource, buildSql(parameterTypes));
        parameterTypes.forEach(type -> sqlUpdate.declareParameter(new SqlParameter(type.intValue())));
        return sqlUpdate;
    }

    public SqlUpdate buildAndCompile()
    {
        var sqlUpdate = build();
        sqlUpdate.compile();
        return sqlUpdate;
    }

    public UpdateFromTableBuilder withColumns(Column... columns)
    {
        includedColumns = new ArrayList<>(List.of(columns));
        excludedColumns = null;
        return this;
    }

    public UpdateFromTableBuilder withoutColumns(Column... columns)
    {
        excludedColumns = new ArrayList<>(List.of(columns));
        includedColumns = null;
        return this;
    }

    public UpdateFromTableBuilder withUpdateAutoIncrementColumns(Column... columns)
    {
        this.updateAutoIncrementColumns = new ArrayList<>(List.of(columns));
        return this;
    }

    @SuppressWarnings("hiding")
    public UpdateFromTableBuilder withCondition(Condition condition)
    {
        this.condition = condition;
        return this;
    }

    public UpdateFromTableBuilder withEqualsCondition(Column column)
    {
        return withCondition(column.eq(getPlaceholder(column)));
    }

    public UpdateFromTableBuilder withoutPlaceholderNames()
    {
        usePlaceholderNames = false;
        return this;
    }

    private String buildSql(List<Integer> parameterTypes)
    {
        var update = update(table);
        table.getColumns().stream().filter(this::isUpdateColumn).forEach(column ->
        {
            update.set(column, getPlaceholder(column));
            parameterTypes.add(Integer.valueOf(column.getSqlType()));
        });
        if (condition != null)
        {
            update.where(condition);
            if (!isEmpty(condition.getPlaceholderSqlTypes()))
            {
                condition.getPlaceholderSqlTypes().forEach(parameterTypes::add);
            }
        }
        else
        {
            var autoIncrementColumn = findAutoIncrementColumn();
            update.where(autoIncrementColumn.eq(getPlaceholder(autoIncrementColumn)));
            parameterTypes.add(Integer.valueOf(autoIncrementColumn.getSqlType()));
        }
        return update.build(dialect);
    }

    private Placeholder getPlaceholder(Column column)
    {
        return usePlaceholderNames ? placeholder(column) : placeholder();
    }

    private boolean isUpdateColumn(Column column)
    {
        var result = true;
        if (includedColumns != null)
        {
            result = includedColumns.stream().anyMatch(col -> col == column);
        }
        if (excludedColumns != null)
        {
            result = excludedColumns.stream().noneMatch(col -> col == column);
        }
        if (column.getColumnDefinition().isAutoIncrement())
        {
            if (updateAutoIncrementColumns != null)
            {
                result = updateAutoIncrementColumns.stream().anyMatch(col -> col == column);
            }
            else
            {
                result = false;
            }
        }
        return result;
    }

    private Column findAutoIncrementColumn()
    {
        var autoIncrementColumns = table.getColumns()
                .stream()
                .filter(column -> column.getColumnDefinition().isAutoIncrement() && !isUpdateAutoIncrementColumn(column))
                .collect(toList());
        if (autoIncrementColumns.size() != 1)
        {
            throw new IllegalArgumentException(MessageFormat
                    .format("the table must exactly have 1 autoIncrement-column, but {0} were found", Integer.valueOf(autoIncrementColumns.size())));
        }
        return autoIncrementColumns.get(0);
    }

    private boolean isUpdateAutoIncrementColumn(Column column)
    {
        return updateAutoIncrementColumns != null && updateAutoIncrementColumns.stream().anyMatch(col -> col == column);
    }
}
