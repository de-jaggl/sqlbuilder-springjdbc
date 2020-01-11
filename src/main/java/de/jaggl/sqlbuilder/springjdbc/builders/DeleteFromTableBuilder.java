package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.domain.Placeholder.placeholder;
import static de.jaggl.sqlbuilder.queries.Queries.deleteFrom;
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

@RequiredArgsConstructor(access = PACKAGE)
public class DeleteFromTableBuilder
{
    private final Table table;
    private final DataSource dataSource;
    private final Dialect dialect;

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

    @SuppressWarnings("hiding")
    public DeleteFromTableBuilder withCondition(Condition condition)
    {
        this.condition = condition;
        return this;
    }

    public DeleteFromTableBuilder withEqualsCondition(Column column)
    {
        return withCondition(column.eq(getPlaceholder(column)));
    }

    public DeleteFromTableBuilder withoutPlaceholderNames()
    {
        usePlaceholderNames = false;
        return this;
    }

    private String buildSql(List<Integer> parameterTypes)
    {
        var delete = deleteFrom(table);
        if (condition != null)
        {
            delete.where(condition);
            if (!isEmpty(condition.getPlaceholderSqlTypes()))
            {
                condition.getPlaceholderSqlTypes().forEach(parameterTypes::add);
            }
        }
        else
        {
            var autoIncrementColumn = findAutoIncrementColumn();
            delete.where(autoIncrementColumn.eq(getPlaceholder(autoIncrementColumn)));
            parameterTypes.add(Integer.valueOf(autoIncrementColumn.getSqlType()));
        }
        return delete.build(dialect);
    }

    private Placeholder getPlaceholder(Column column)
    {
        return usePlaceholderNames ? placeholder(column) : placeholder();
    }

    private Column findAutoIncrementColumn()
    {
        var autoIncrementColumns = table.getColumns()
                .stream()
                .filter(column -> column.getColumnDefinition().isAutoIncrement())
                .collect(toList());
        if (autoIncrementColumns.size() != 1)
        {
            throw new IllegalArgumentException(MessageFormat
                    .format("the table must exactly have 1 autoIncrement-column, but {0} were found", Integer.valueOf(autoIncrementColumns.size())));
        }
        return autoIncrementColumns.get(0);
    }
}
