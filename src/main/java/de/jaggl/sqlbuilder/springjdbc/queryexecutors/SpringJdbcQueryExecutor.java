package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import org.springframework.jdbc.core.JdbcTemplate;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.ExecutableQuery;
import de.jaggl.sqlbuilder.queries.UpdatebleQuery;
import de.jaggl.sqlbuilder.queryexecutor.QueryExecutor;
import de.jaggl.sqlbuilder.queryexecutor.RowExtractor;
import de.jaggl.sqlbuilder.queryexecutor.SelectQueryExecutor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringJdbcQueryExecutor implements QueryExecutor
{
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public SpringJdbcQueryExecutor(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = Dialect.getDefault();
    }

    public SpringJdbcQueryExecutor(JdbcTemplate jdbcTemplate, String dialectName)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = Dialect.forName(dialectName);
    }

    @Override
    public <T> SelectQueryExecutor<T> select(RowExtractor<T> rowExtractor)
    {
        return new SpringJdbcSelectQueryExecutor<>(jdbcTemplate, rowExtractor::mapRow, dialect);
    }

    @Override
    public <T> SelectQueryExecutor<T> select(Class<T> elementType)
    {
        return new SpringJdbcSelectQueryExecutor<>(jdbcTemplate, elementType, dialect);
    }

    @Override
    public int execute(UpdatebleQuery updatebleQuery)
    {
        return jdbcTemplate.update(updatebleQuery.build(dialect));
    }

    @Override
    public void execute(ExecutableQuery executableQuery)
    {
        jdbcTemplate.execute(executableQuery.build(dialect));
    }
}
