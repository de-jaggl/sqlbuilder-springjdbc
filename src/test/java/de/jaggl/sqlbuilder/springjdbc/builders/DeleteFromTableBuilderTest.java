package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.dialect.Dialects.MYSQL;
import static de.jaggl.sqlbuilder.domain.Placeholder.placeholder;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.delete;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.deleteBuilder;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createNiceMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import de.jaggl.sqlbuilder.columns.number.integer.BigIntColumn;
import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.schema.Schema;
import de.jaggl.sqlbuilder.schema.Table;

public class DeleteFromTableBuilderTest
{
    protected static final Schema SCHEMA = Schema.create("schema");

    protected static final Table TABLE = SCHEMA.table("table");

    protected static final BigIntColumn COLUMN1 = TABLE.bigIntColumn("column1").autoIncrement().build();
    protected static final VarCharColumn COLUMN2 = TABLE.varCharColumn("column2").build();
    protected static final VarCharColumn COLUMN3 = TABLE.varCharColumn("column3").build();
    protected static final VarCharColumn COLUMN4 = TABLE.varCharColumn("column4").build();

    protected static final Table TABLE2 = Table.create("table2");

    protected static final BigIntColumn COLUMN5 = TABLE2.bigIntColumn("column5").autoIncrement().build();
    protected static final BigIntColumn COLUMN6 = TABLE2.bigIntColumn("column6").autoIncrement().build();
    protected static final VarCharColumn COLUMN7 = TABLE2.varCharColumn("column7").build();

    protected static final Table TABLE3 = Table.create("table3");

    protected static final VarCharColumn COLUMN8 = TABLE3.varCharColumn("column8").build();

    @Test
    void testSqlUpdate() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = delete(TABLE, dataSource);
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSqlUpdateWithDialect() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = delete(TABLE, dataSource, MYSQL);
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSqlUpdateWithDialectName() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = delete(TABLE, dataSource, "MYSQL");
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSqlUpdateBuilderWithoutPlaceholderNames() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = deleteBuilder(TABLE, dataSource, MYSQL).withoutPlaceholderNames().build();
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column1` = ?");
    }

    @Test
    void testBuildWithoutAutoIncrementColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        assertThatThrownBy(() -> delete(TABLE3, dataSource)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the table must exactly have 1 autoIncrement-column, but 0 were found");
        verifyAll();
    }

    @Test
    void testBuildWithMultipleAutoIncrementColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        assertThatThrownBy(() -> delete(TABLE2, dataSource, "MYSQL")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the table must exactly have 1 autoIncrement-column, but 2 were found");
        verifyAll();
    }

    @Test
    void testSqlUpdateBuilderWithCondition() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = deleteBuilder(TABLE, dataSource).withCondition(COLUMN3.eq(placeholder(COLUMN3))).build();
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column3` = :column3");
    }

    @Test
    void testSqlUpdateBuilderWithConditionWithoutPlaceholders() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = deleteBuilder(TABLE, dataSource, "MYSQL").withCondition(COLUMN3.isNull()).build();
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column3` IS NULL");
    }

    @Test
    void testSqlUpdateBuilderWithEqualsCondition() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var delete = deleteBuilder(TABLE, dataSource, "MYSQL").withEqualsCondition(COLUMN3).build();
        verifyAll();

        assertThat(delete.getSql()).isEqualTo("DELETE FROM `schema`.`table` WHERE `schema`.`table`.`column3` = :column3");
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
