package de.jaggl.sqlbuilder.springjdbc.builders;

import static org.springframework.dao.support.DataAccessUtils.singleResult;

import java.util.Optional;

import org.springframework.jdbc.object.SqlQuery;

import lombok.AllArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
@AllArgsConstructor
public class SimpleSelectOne<T>
{
    SqlQuery<T> sqlQuery;

    public Optional<T> execute(long id)
    {
        return Optional.ofNullable(singleResult(sqlQuery.execute(id)));
    }
}
