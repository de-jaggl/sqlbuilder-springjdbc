package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import static java.util.Optional.ofNullable;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.Query;
import de.jaggl.sqlbuilder.queryexecutor.SelectQueryExecutor;
import lombok.RequiredArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SpringJdbcSelectQueryExecutor<T> implements SelectQueryExecutor<T>
{
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<T> rowMapper;
    private Class<T> elementType;
    private final Dialect dialect;

    public SpringJdbcSelectQueryExecutor(JdbcTemplate jdbcTemplate, Class<T> elementType, Dialect dialect)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = null;
        this.elementType = elementType;
        this.dialect = dialect;
    }

    @Override
    public List<T> query(Query select)
    {
        if (rowMapper != null)
        {
            return jdbcTemplate.query(select.build(dialect), rowMapper);
        }
        return jdbcTemplate.queryForList(select.build(dialect), elementType);
    }

    @Override
    public Optional<T> queryOne(Query select)
    {
        if (rowMapper != null)
        {
            return ofNullable(singleResult(jdbcTemplate.query(select.build(dialect), rowMapper)));
        }
        return ofNullable(singleResult(jdbcTemplate.queryForList(select.build(dialect), elementType)));
    }

    @Override
    public T queryExactOne(Query query)
    {
        if (rowMapper != null)
        {
            return jdbcTemplate.queryForObject(query.build(dialect), rowMapper);
        }
        return jdbcTemplate.queryForObject(query.build(dialect), elementType);
    }
}
