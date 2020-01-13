package de.jaggl.sqlbuilder.springjdbc.repository;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleDeleteOne;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleInsert;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleSelectAll;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleSelectOne;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleUpdate;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.RowMapperAndParamSource;

/**
 * @author Martin Schumacher
 *
 * @since 1.2.0
 */
public class SimpleJdbcRepository<T>
{
    private SimpleInsert<T> insert;
    private SimpleUpdate<T> update;
    private SimpleDeleteOne deleteOne;
    private SimpleSelectOne<T> selectOne;
    private SimpleSelectAll<T> selectAll;

    public SimpleJdbcRepository(Table table, DataSource dataSource, RowMapperAndParamSource<T> rowMapper, Dialect dialect)
    {
        insert = SimpleOperations.insert(table, dataSource, rowMapper);
        update = SimpleOperations.update(table, dataSource, dialect, rowMapper);
        deleteOne = SimpleOperations.deleteOne(table, dataSource, dialect);
        selectOne = SimpleOperations.selectOne(table, dataSource, dialect, rowMapper);
        selectAll = SimpleOperations.selectAll(table, dataSource, dialect, rowMapper);
    }

    public SimpleJdbcRepository(Table table, DataSource dataSource, RowMapperAndParamSource<T> rowMapper, String dialectName)
    {
        this(table, dataSource, rowMapper, Dialect.forName(dialectName));
    }

    public SimpleJdbcRepository(Table table, DataSource dataSource, RowMapperAndParamSource<T> rowMapper)
    {
        this(table, dataSource, rowMapper, Dialect.getDefault());
    }

    public long insert(T data)
    {
        return insert.execute(data);
    }

    public long[] insert(List<T> data)
    {
        return insert.execute(data);
    }

    public long update(T data)
    {
        return update.execute(data);
    }

    public long delete(long id)
    {
        return deleteOne.execute(id);
    }

    public List<T> getAll()
    {
        return selectAll.execute();
    }

    public Optional<T> getById(long id)
    {
        return selectOne.execute(id);
    }
}
