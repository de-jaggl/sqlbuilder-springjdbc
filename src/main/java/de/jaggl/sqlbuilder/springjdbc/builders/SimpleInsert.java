package de.jaggl.sqlbuilder.springjdbc.builders;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import de.jaggl.sqlbuilder.springjdbc.builders.utils.KeySetter;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.ParamSource;
import lombok.AllArgsConstructor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
@AllArgsConstructor
public class SimpleInsert<T>
{
    SimpleJdbcInsert simpleJdbcInsert;
    ParamSource<T> paramSource;
    boolean considerKey;

    @SuppressWarnings("unchecked")
    public long execute(T data)
    {
        if (considerKey)
        {
            long key = simpleJdbcInsert.executeAndReturnKey(paramSource.getParams(data)).longValue();
            if (KeySetter.class.isAssignableFrom(paramSource.getClass()))
            {
                ((KeySetter<Object>) paramSource).setKey(data, key);
            }
            return key;
        }
        return simpleJdbcInsert.execute(paramSource.getParams(data));
    }

    public long[] execute(List<T> data)
    {
        return stream(simpleJdbcInsert.executeBatch(data.stream().map(entry -> paramSource.getParams(entry)).collect(toList()).toArray(new Map[0])))
                .mapToLong(value -> (long) value)
                .toArray();
    }
}
