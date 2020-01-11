package de.jaggl.sqlbuilder.springjdbc.builders.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.easymock.PowerMock.createStrictMock;

import java.util.HashMap;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.RowMapper;

class SingletonRowMapperSqlQueryTest
{
    @Test
    void testAll()
    {
        var rowMapper = createStrictMock(RowMapper.class);
        var dataSource = createStrictMock(DataSource.class);

        @SuppressWarnings("unchecked")
        var sqlQuery = new SingletonRowMapperSqlQuery<Object>(dataSource, "anySql", rowMapper);

        assertThat(sqlQuery.newRowMapper(new Object[0], new HashMap<>())).isSameAs(rowMapper);

    }
}
