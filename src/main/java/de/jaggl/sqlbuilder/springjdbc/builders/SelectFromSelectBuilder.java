package de.jaggl.sqlbuilder.springjdbc.builders;

import static lombok.AccessLevel.PACKAGE;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlQuery;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.Select;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.SingletonRowMapperSqlQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PACKAGE)
public class SelectFromSelectBuilder<T>
{
    private final Select select;
    private final DataSource dataSource;
    private final Dialect dialect;
    private final RowMapper<T> rowMapper;

    public SqlQuery<T> build()
    {
        return new SingletonRowMapperSqlQuery<>(dataSource, select.build(dialect), rowMapper);
    }

    public SqlQuery<T> buildAndCompile()
    {
        var sqlQuery = build();
        sqlQuery.compile();
        return sqlQuery;
    }
}
