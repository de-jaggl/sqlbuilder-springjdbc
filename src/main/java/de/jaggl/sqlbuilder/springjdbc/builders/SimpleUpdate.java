package de.jaggl.sqlbuilder.springjdbc.builders;

import org.springframework.jdbc.object.SqlUpdate;

import de.jaggl.sqlbuilder.springjdbc.builders.utils.ParamSource;
import lombok.AllArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
@AllArgsConstructor
public class SimpleUpdate<T>
{
    SqlUpdate sqlUpdate;
    ParamSource<T> paramSource;

    public long execute(T data)
    {
        return sqlUpdate.updateByNamedParam(paramSource.getParams(data));
    }
}
