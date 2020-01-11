package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.dialect.Dialects.MYSQL;
import static de.jaggl.sqlbuilder.queries.Queries.select;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.select;
import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.easymock.PowerMock.createNiceMock;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import de.jaggl.sqlbuilder.schema.Table;

class SelectFromSelectBuilderTest
{
    private static final Table TABLE = Table.create("table");

    @Test
    void testSqlQuery()
    {
        var dataSource = createNiceMock(DataSource.class);

        var select = select().from(TABLE);

        var sqlQuery = select(select, dataSource, (rs, rowNum) -> rs.getString("column"));

        assertThat(sqlQuery.getSql()).isEqualTo("SELECT * FROM `table`");
    }

    @Test
    void testSqlQueryWithDialect()
    {
        var dataSource = createNiceMock(DataSource.class);

        var select = select().from(TABLE);

        var sqlQuery = select(select, dataSource, MYSQL, (rs, rowNum) -> rs.getString("column"));

        assertThat(sqlQuery.getSql()).isEqualTo("SELECT * FROM `table`");
    }

    @Test
    void testSqlQueryWithDialectName()
    {
        var dataSource = createNiceMock(DataSource.class);

        var select = select().from(TABLE);

        var sqlQuery = select(select, dataSource, "MYSQL", (rs, rowNum) -> rs.getString("column"));

        assertThat(sqlQuery.getSql()).isEqualTo("SELECT * FROM `table`");
    }
}
