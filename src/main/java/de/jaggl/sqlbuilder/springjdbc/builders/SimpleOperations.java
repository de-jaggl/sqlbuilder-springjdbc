package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.insertBuilder;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.selectBuilder;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.updateBuilder;
import static lombok.AccessLevel.PRIVATE;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.ParamSource;
import lombok.NoArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
@NoArgsConstructor(access = PRIVATE)
public class SimpleOperations
{
    public static <T> SimpleInsert<T> insert(Table table, DataSource dataSource, ParamSource<T> paramSource)
    {
        return new SimpleInsert<>(insertBuilder(table, dataSource)
                .buildAndCompile(), paramSource, table.getColumns().stream().anyMatch(col -> col.getColumnDefinition().isAutoIncrement()));
    }

    public static <T> SimpleUpdate<T> update(Table table, DataSource dataSource, Dialect dialect, ParamSource<T> paramSource)
    {
        return new SimpleUpdate<>(updateBuilder(table, dataSource, dialect).buildAndCompile(), paramSource);
    }

    public static <T> SimpleUpdate<T> update(Table table, DataSource dataSource, String dialectName, ParamSource<T> paramSource)
    {
        return update(table, dataSource, Dialect.forName(dialectName), paramSource);
    }

    public static <T> SimpleUpdate<T> update(Table table, DataSource dataSource, ParamSource<T> paramSource)
    {
        return update(table, dataSource, Dialect.getDefault(), paramSource);
    }

    public static SimpleDeleteOne deleteOne(Table table, DataSource dataSource, Dialect dialect)
    {
        return new SimpleDeleteOne(new DeleteFromTableBuilder(table, dataSource, dialect).withoutPlaceholderNames().buildAndCompile());
    }

    public static SimpleDeleteOne deleteOne(Table table, DataSource dataSource, String dialectName)
    {
        return deleteOne(table, dataSource, Dialect.forName(dialectName));
    }

    public static SimpleDeleteOne deleteOne(Table table, DataSource dataSource)
    {
        return deleteOne(table, dataSource, Dialect.getDefault());
    }

    public static <T> SimpleSelectAll<T> selectAll(Table table, DataSource dataSource, Dialect dialect, RowMapper<T> rowMapper)
    {
        return new SimpleSelectAll<>(selectBuilder(table, dataSource, dialect, rowMapper).buildSelectAllAndCompile());
    }

    public static <T> SimpleSelectAll<T> selectAll(Table table, DataSource dataSource, String dialectName, RowMapper<T> rowMapper)
    {
        return selectAll(table, dataSource, Dialect.forName(dialectName), rowMapper);
    }

    public static <T> SimpleSelectAll<T> selectAll(Table table, DataSource dataSource, RowMapper<T> rowMapper)
    {
        return selectAll(table, dataSource, Dialect.getDefault(), rowMapper);
    }

    public static <T> SimpleSelectOne<T> selectOne(Table table, DataSource dataSource, Dialect dialect, RowMapper<T> rowMapper)
    {
        return new SimpleSelectOne<>(selectBuilder(table, dataSource, dialect, rowMapper).withoutPlaceholderNames().buildSelectOneAndCompile());
    }

    public static <T> SimpleSelectOne<T> selectOne(Table table, DataSource dataSource, String dialectName, RowMapper<T> rowMapper)
    {
        return selectOne(table, dataSource, Dialect.forName(dialectName), rowMapper);
    }

    public static <T> SimpleSelectOne<T> selectOne(Table table, DataSource dataSource, RowMapper<T> rowMapper)
    {
        return selectOne(table, dataSource, Dialect.getDefault(), rowMapper);
    }
}
