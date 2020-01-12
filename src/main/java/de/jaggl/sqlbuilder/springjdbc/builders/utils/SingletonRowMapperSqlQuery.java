package de.jaggl.sqlbuilder.springjdbc.builders.utils;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlQuery;

/**
 * @author Martin Schumacher
 *
 * @since 1.0.0
 */
public class SingletonRowMapperSqlQuery<T> extends SqlQuery<T>
{
    private final RowMapper<T> rowMapper;

    public SingletonRowMapperSqlQuery(DataSource dataSource, String sql, RowMapper<T> rowMapper)
    {
        super(dataSource, sql);
        this.rowMapper = rowMapper;
    }

    @Override
    protected RowMapper<T> newRowMapper(Object[] parameters, Map<?, ?> context)
    {
        return rowMapper;
    }
}
