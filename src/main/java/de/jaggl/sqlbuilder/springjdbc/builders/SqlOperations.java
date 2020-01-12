package de.jaggl.sqlbuilder.springjdbc.builders;

import static lombok.AccessLevel.PRIVATE;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.Delete;
import de.jaggl.sqlbuilder.queries.Insert;
import de.jaggl.sqlbuilder.queries.Select;
import de.jaggl.sqlbuilder.queries.Update;
import de.jaggl.sqlbuilder.schema.Table;
import lombok.NoArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.0.0
 */
@NoArgsConstructor(access = PRIVATE)
public class SqlOperations
{
    public static <T> SqlQuery<T> select(Select select, DataSource dataSource, Dialect dialect, RowMapper<T> rowMapper)
    {
        return new SelectFromSelectBuilder<>(select, dataSource, dialect, rowMapper).buildAndCompile();
    }

    public static <T> SqlQuery<T> select(Select select, DataSource dataSource, String dialectName, RowMapper<T> rowMapper)
    {
        return select(select, dataSource, Dialect.forName(dialectName), rowMapper);
    }

    public static <T> SqlQuery<T> select(Select select, DataSource dataSource, RowMapper<T> rowMapper)
    {
        return select(select, dataSource, Dialect.getDefault(), rowMapper);
    }

    public static <T> SelectFromTableBuilder<T> selectBuilder(Table table, DataSource dataSource, Dialect dialect, RowMapper<T> rowMapper)
    {
        return new SelectFromTableBuilder<>(table, dataSource, dialect, rowMapper);
    }

    public static <T> SelectFromTableBuilder<T> selectBuilder(Table table, DataSource dataSource, String dialectName, RowMapper<T> rowMapper)
    {
        return selectBuilder(table, dataSource, Dialect.forName(dialectName), rowMapper);
    }

    public static <T> SelectFromTableBuilder<T> selectBuilder(Table table, DataSource dataSource, RowMapper<T> rowMapper)
    {
        return selectBuilder(table, dataSource, Dialect.getDefault(), rowMapper);
    }

    public static SqlUpdate insert(Insert insert, DataSource dataSource, Dialect dialect)
    {
        return new InsertFromInsertBuilder(insert, dataSource, dialect).buildAndCompile();
    }

    public static SqlUpdate insert(Insert insert, DataSource dataSource, String dialectName)
    {
        return insert(insert, dataSource, Dialect.forName(dialectName));
    }

    public static SqlUpdate insert(Insert insert, DataSource dataSource)
    {
        return insert(insert, dataSource, Dialect.getDefault());
    }

    public static InsertFromTableBuilder insertBuilder(Table table, DataSource dataSource)
    {
        return new InsertFromTableBuilder(table, dataSource);
    }

    public static SqlUpdate update(Update update, DataSource dataSource, Dialect dialect)
    {
        return new UpdateFromUpdateBuilder(update, dataSource, dialect).buildAndCompile();
    }

    public static SqlUpdate update(Update update, DataSource dataSource, String dialectName)
    {
        return update(update, dataSource, Dialect.forName(dialectName));
    }

    public static SqlUpdate update(Update update, DataSource dataSource)
    {
        return update(update, dataSource, Dialect.getDefault());
    }

    public static UpdateFromTableBuilder updateBuilder(Table table, DataSource dataSource, Dialect dialect)
    {
        return new UpdateFromTableBuilder(table, dataSource, dialect);
    }

    public static UpdateFromTableBuilder updateBuilder(Table table, DataSource dataSource, String dialectName)
    {
        return updateBuilder(table, dataSource, Dialect.forName(dialectName));
    }

    public static UpdateFromTableBuilder updateBuilder(Table table, DataSource dataSource)
    {
        return updateBuilder(table, dataSource, Dialect.getDefault());
    }

    public static SqlUpdate delete(Delete delete, DataSource dataSource, Dialect dialect)
    {
        return new DeleteFromDeleteBuilder(delete, dataSource, dialect).buildAndCompile();
    }

    public static SqlUpdate delete(Delete delete, DataSource dataSource, String dialectName)
    {
        return delete(delete, dataSource, Dialect.forName(dialectName));
    }

    public static SqlUpdate delete(Delete delete, DataSource dataSource)
    {
        return delete(delete, dataSource, Dialect.getDefault());
    }

    public static DeleteFromTableBuilder deleteBuilder(Table table, DataSource dataSource, Dialect dialect)
    {
        return new DeleteFromTableBuilder(table, dataSource, dialect);
    }

    public static DeleteFromTableBuilder deleteBuilder(Table table, DataSource dataSource, String dialectName)
    {
        return deleteBuilder(table, dataSource, Dialect.forName(dialectName));
    }

    public static DeleteFromTableBuilder deleteBuilder(Table table, DataSource dataSource)
    {
        return deleteBuilder(table, dataSource, Dialect.getDefault());
    }
}
