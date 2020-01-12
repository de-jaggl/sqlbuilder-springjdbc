package de.jaggl.sqlbuilder.springjdbc.builders.utils;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
public interface KeySetter<T>
{
    void setKey(T data, long key);
}
