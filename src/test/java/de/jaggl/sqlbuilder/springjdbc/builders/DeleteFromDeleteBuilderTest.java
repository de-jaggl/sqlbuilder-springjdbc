package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.dialect.Dialects.MYSQL;
import static de.jaggl.sqlbuilder.domain.Placeholder.placeholder;
import static de.jaggl.sqlbuilder.queries.Queries.deleteFrom;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.delete;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createNiceMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.schema.Schema;
import de.jaggl.sqlbuilder.schema.Table;

class DeleteFromDeleteBuilderTest
{
    private static final Schema SCHEMA = Schema.create("schema");

    private static final Table TABLE = SCHEMA.table("table");

    private static final VarCharColumn COLUMN1 = TABLE.varCharColumn("column1").build();

    @Test
    void testDelete() throws SQLException
    {
        var dataSource = getDataSourceMock();

        var delete = deleteFrom(TABLE).where(COLUMN1.eq(placeholder(COLUMN1)));

        replayAll();
        var sqlUpdate = delete(delete, dataSource);
        verifyAll();

        assertThat(sqlUpdate.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testDeleteWithDialect() throws SQLException
    {
        var dataSource = getDataSourceMock();

        var delete = deleteFrom(TABLE);

        replayAll();
        var sqlUpdate = delete(delete, dataSource, MYSQL);
        verifyAll();

        assertThat(sqlUpdate.getSql()).isEqualTo("DELETE FROM `schema`.`table`");
    }

    @Test
    void testDeleteWithDialectName() throws SQLException
    {
        var dataSource = getDataSourceMock();

        var delete = deleteFrom(TABLE);

        replayAll();
        var sqlUpdate = delete(delete, dataSource, "MYSQL");
        verifyAll();

        assertThat(sqlUpdate.getSql()).isEqualTo("DELETE FROM `schema`.`table`");
    }

    @SuppressWarnings("resource")
    private static DataSource getDataSourceMock() throws SQLException
    {
        var connection = createNiceMock(Connection.class);
        var dataSource = createNiceMock(DataSource.class);
        var metaData = createNiceMock(DatabaseMetaData.class);

        expect(dataSource.getConnection()).andReturn(connection).anyTimes();
        expect(connection.getMetaData()).andReturn(metaData).anyTimes();
        expect(metaData.getDatabaseProductName()).andReturn("anyName").anyTimes();
        expect(metaData.getUserName()).andReturn("anyUserName").anyTimes();
        expect(Boolean.valueOf(metaData.supportsGetGeneratedKeys())).andReturn(TRUE).anyTimes();

        return dataSource;
    }
}
