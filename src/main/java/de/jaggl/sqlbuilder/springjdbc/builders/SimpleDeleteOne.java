package de.jaggl.sqlbuilder.springjdbc.builders;

import org.springframework.jdbc.object.SqlUpdate;

import lombok.AllArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
@AllArgsConstructor
public class SimpleDeleteOne
{
    SqlUpdate sqlUpdate;

    public long execute(long id)
    {
        return sqlUpdate.update(id);
    }
}
