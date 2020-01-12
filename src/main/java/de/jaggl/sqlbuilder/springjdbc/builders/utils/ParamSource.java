package de.jaggl.sqlbuilder.springjdbc.builders.utils;

import java.util.Map;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
public interface ParamSource<T>
{
    Map<String, Object> getParams(T data);
}
