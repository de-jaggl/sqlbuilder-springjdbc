package de.jaggl.sqlbuilder.springjdbc.builders;

import java.util.List;

import org.springframework.jdbc.object.SqlQuery;

import lombok.AllArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
@AllArgsConstructor
public class SimpleSelectAll<T>
{
    SqlQuery<T> sqlQuery;

    public List<T> execute()
    {
        return sqlQuery.execute();
    }
}
