package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.domain.Placeholder.placeholder;
import static de.jaggl.sqlbuilder.queries.Queries.select;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlQuery;

import de.jaggl.sqlbuilder.columns.Column;
import de.jaggl.sqlbuilder.conditions.Condition;
import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.domain.Placeholder;
import de.jaggl.sqlbuilder.domain.Selectable;
import de.jaggl.sqlbuilder.queries.Select;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.SingletonRowMapperSqlQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PACKAGE)
public class SelectFromTableBuilder<T>
{
    private final Table table;
    private final DataSource dataSource;
    private final Dialect dialect;
    private final RowMapper<T> rowMapper;

    private Selectable[] selectables;

    private Condition selectOneCondition;
    private Condition selectAllCondition;

    private boolean usePlaceholderNames = true;

    public SqlQuery<T> buildSelectAll()
    {
        var parameterTypes = new ArrayList<Integer>();
        var sqlQuery = sqlQuery(buildSqlForSelectAll(parameterTypes));
        parameterTypes.forEach(type -> sqlQuery.declareParameter(new SqlParameter(type.intValue())));
        return sqlQuery;
    }

    public SqlQuery<T> buildSelectOne()
    {
        var parameterTypes = new ArrayList<Integer>();
        var sqlQuery = sqlQuery(buildSqlForSelectOne(parameterTypes));
        parameterTypes.forEach(type -> sqlQuery.declareParameter(new SqlParameter(type.intValue())));
        return sqlQuery;
    }

    private SqlQuery<T> sqlQuery(String sql)
    {
        return new SingletonRowMapperSqlQuery<>(dataSource, sql, rowMapper);
    }

    public SqlQuery<T> buildSelectAllAndCompile()
    {
        var sqlUpdate = buildSelectAll();
        sqlUpdate.compile();
        return sqlUpdate;
    }

    public SqlQuery<T> buildSelectOneAndCompile()
    {
        var sqlUpdate = buildSelectOne();
        sqlUpdate.compile();
        return sqlUpdate;
    }

    public SelectFromTableBuilder<T> withSelectables(@SuppressWarnings("hiding") Selectable... selectables)
    {
        this.selectables = selectables;
        return this;
    }

    public SelectFromTableBuilder<T> withSelectOneCondition(Condition condition)
    {
        this.selectOneCondition = condition;
        return this;
    }

    public SelectFromTableBuilder<T> withSelectAllCondition(Condition condition)
    {
        this.selectAllCondition = condition;
        return this;
    }

    public SelectFromTableBuilder<T> withSelectOneEqualsCondition(Column column)
    {
        return withSelectOneCondition(column.eq(getPlaceholder(column)));
    }

    public SelectFromTableBuilder<T> withSelectAllEqualsCondition(Column column)
    {
        return withSelectAllCondition(column.eq(getPlaceholder(column)));
    }

    public SelectFromTableBuilder<T> withoutPlaceholderNames()
    {
        usePlaceholderNames = false;
        return this;
    }

    private String buildSqlForSelectAll(List<Integer> parameterTypes)
    {
        var select = select().from(table);
        if (selectables != null)
        {
            select.select(selectables);
        }
        appendCondition(select, selectAllCondition, parameterTypes);
        return select.build(dialect);
    }

    private String buildSqlForSelectOne(List<Integer> parameterTypes)
    {
        var select = select().from(table);
        if (selectables != null)
        {
            select.select(selectables);
        }
        if (!appendCondition(select, selectOneCondition, parameterTypes))
        {
            var autoIncrementColumn = findAutoIncrementColumn();
            select.where(autoIncrementColumn.eq(getPlaceholder(autoIncrementColumn)));
            parameterTypes.add(Integer.valueOf(autoIncrementColumn.getSqlType()));
        }
        return select.build(dialect);
    }

    private static boolean appendCondition(Select select, Condition condition, List<Integer> parameterTypes)
    {
        if (condition != null)
        {
            select.where(condition);
            if (!isEmpty(condition.getPlaceholderSqlTypes()))
            {
                condition.getPlaceholderSqlTypes().forEach(parameterTypes::add);
            }
            return true;
        }
        return false;
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
