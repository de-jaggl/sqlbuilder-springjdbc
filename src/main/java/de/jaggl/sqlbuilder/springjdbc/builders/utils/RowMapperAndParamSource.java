package de.jaggl.sqlbuilder.springjdbc.builders.utils;

import org.springframework.jdbc.core.RowMapper;

import de.jaggl.sqlbuilder.queryexecutor.RowExtractor;

/**
 * @author Martin Schumacher
 *
 * @since 1.1.1
 */
public interface RowMapperAndParamSource<T> extends RowMapper<T>, ParamSource<T>, RowExtractor<T>
{
    // nothing to do here
}
