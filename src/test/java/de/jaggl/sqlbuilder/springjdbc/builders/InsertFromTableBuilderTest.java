package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.insert;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.insertBuilder;
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

import de.jaggl.sqlbuilder.columns.number.integer.BigIntColumn;
import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.schema.Schema;
import de.jaggl.sqlbuilder.schema.Table;

class InsertFromTableBuilderTest
{
    protected static final Schema SCHEMA = Schema.create("schema");

    protected static final Table TABLE = SCHEMA.table("table");

    protected static final BigIntColumn COLUMN1 = TABLE.bigIntColumn("column1").autoIncrement().build();
    protected static final VarCharColumn COLUMN2 = TABLE.varCharColumn("column2").build();
    protected static final VarCharColumn COLUMN3 = TABLE.varCharColumn("column3").build();
    protected static final VarCharColumn COLUMN4 = TABLE.varCharColumn("column4").build();

    protected static final Table TABLE2 = Table.create("table2");

    protected static final VarCharColumn COLUMN5 = TABLE2.varCharColumn("column5").build();

    protected static final Table TABLE3 = Table.create("table3");

    protected static final BigIntColumn COLUMN6 = TABLE3.bigIntColumn("column6").autoIncrement().build();
    protected static final BigIntColumn COLUMN7 = TABLE3.bigIntColumn("column7").autoIncrement().build();
    protected static final VarCharColumn COLUMN8 = TABLE3.varCharColumn("column8").build();

    @Test
    void testBuild() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var simpleJdbcInsert = insert(TABLE, dataSource);
        assertThat(simpleJdbcInsert.getInsertString()).isEqualTo("INSERT INTO schema.table (column2, column3, column4) VALUES(?, ?, ?)");
        assertThat(simpleJdbcInsert.getGeneratedKeyNames()).containsExactly("column1");
        verifyAll();
    }

    @Test
    void testBuildWithColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var simpleJdbcInsert = insertBuilder(TABLE, dataSource).withColumns(COLUMN1, COLUMN3, COLUMN5).buildAndCompile();
        assertThat(simpleJdbcInsert.getInsertString()).isEqualTo("INSERT INTO schema.table (column3) VALUES(?)");
        assertThat(simpleJdbcInsert.getGeneratedKeyNames()).containsExactly("column1");
        verifyAll();
    }

    @Test
    void testBuildWithColumnsAndSettingGeneratedKeyColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var simpleJdbcInsert = insertBuilder(TABLE, dataSource).withColumns(COLUMN3, COLUMN5)
                .withSettingGeneratedKeyColumns(COLUMN1)
                .buildAndCompile();
        assertThat(simpleJdbcInsert.getInsertString()).isEqualTo("INSERT INTO schema.table (column1, column3) VALUES(?, ?)");
        assertThat(simpleJdbcInsert.getGeneratedKeyNames()).isEmpty();
        verifyAll();
    }

    @Test
    void testBuildWithColumnsSettingGeneratedKeyColumnsAndMultipleAUtoIncrementColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var simpleJdbcInsert = insertBuilder(TABLE3, dataSource).withSettingGeneratedKeyColumns(COLUMN6).buildAndCompile();
        assertThat(simpleJdbcInsert.getInsertString()).isEqualTo("INSERT INTO table3 (column6, column8) VALUES(?, ?)");
        assertThat(simpleJdbcInsert.getGeneratedKeyNames()).containsExactly("column7");
        verifyAll();
    }

    @Test
    void testBuildWithoutColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var simpleJdbcInsert = insertBuilder(TABLE, dataSource).withoutColumns(COLUMN1, COLUMN3).buildAndCompile();
        assertThat(simpleJdbcInsert.getInsertString()).isEqualTo("INSERT INTO schema.table (column2, column4) VALUES(?, ?)");
        assertThat(simpleJdbcInsert.getGeneratedKeyNames()).containsExactly("column1");
        verifyAll();
    }

    @Test
    void testBuildWithoutAutoIncrementColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var simpleJdbcInsert = insert(TABLE2, dataSource);
        assertThat(simpleJdbcInsert.getInsertString()).isEqualTo("INSERT INTO table2 (column5) VALUES(?)");
        assertThat(simpleJdbcInsert.getGeneratedKeyNames()).isEmpty();
        verifyAll();
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
