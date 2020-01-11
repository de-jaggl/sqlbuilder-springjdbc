package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.dialect.Dialects.MYSQL;
import static de.jaggl.sqlbuilder.domain.Placeholder.placeholder;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.selectAll;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.selectBuilder;
import static de.jaggl.sqlbuilder.springjdbc.builders.SqlOperations.selectOne;
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

public class SelectFromTableBuilderTest
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
    void testSelectOne() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectOne(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1"));
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSelectAll() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectAll(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1"));
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table`");
    }

    @Test
    void testSelectOneWithDialect() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectOne(TABLE, dataSource, MYSQL, (rs, rowNum) -> rs.getString("column1"));
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSelectAllWithDialect() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectOne(TABLE, dataSource, MYSQL, (rs, rowNum) -> rs.getString("column1"));
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSelectOneWithDialectName() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectOne(TABLE, dataSource, "MYSQL", (rs, rowNum) -> rs.getString("column1"));
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column1` = :column1");
    }

    @Test
    void testSelectAllWithDialectName() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectAll(TABLE, dataSource, "MYSQL", (rs, rowNum) -> rs.getString("column1"));
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table`");
    }

    @Test
    void testSelectOneBuilderWithoutPlaceholderNames() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, "MYSQL", (rs, rowNum) -> rs.getString("column1")).withoutPlaceholderNames().buildSelectOne();
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column1` = ?");
    }

    @Test
    void testBuildWithoutAutoIncrementColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        assertThatThrownBy(() -> selectOne(TABLE3, dataSource, (rs, rowNum) -> rs.getString("column1"))).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the table must exactly have 1 autoIncrement-column, but 0 were found");
        verifyAll();
    }

    @Test
    void testBuildWithMultipleAutoIncrementColumns() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        assertThatThrownBy(() -> selectOne(TABLE2, dataSource, (rs, rowNum) -> rs.getString("column1"))).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the table must exactly have 1 autoIncrement-column, but 2 were found");
        verifyAll();
    }

    @Test
    void testSelectBuilderWithSelectablesAndSelectOneCondition() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1")).withSelectables(COLUMN1, COLUMN2)
                .withSelectOneCondition(COLUMN3.eq(placeholder(COLUMN3)))
                .buildSelectOne();
        verifyAll();

        assertThat(select.getSql())
                .isEqualTo("SELECT `schema`.`table`.`column1`, `schema`.`table`.`column2` FROM `schema`.`table` WHERE `schema`.`table`.`column3` = :column3");
    }

    @Test
    void testSelectBuilderWithSelectablesAndSelectAllCondition() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1")).withSelectables(COLUMN1, COLUMN2)
                .withSelectAllCondition(COLUMN3.eq(placeholder(COLUMN3)))
                .buildSelectAll();
        verifyAll();

        assertThat(select.getSql())
                .isEqualTo("SELECT `schema`.`table`.`column1`, `schema`.`table`.`column2` FROM `schema`.`table` WHERE `schema`.`table`.`column3` = :column3");
    }

    @Test
    void testSelectBuilderWithSelectOneConditionWithoutPlaceholders() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1")).withSelectOneCondition(COLUMN3.isNull()).buildSelectOne();
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column3` IS NULL");
    }

    @Test
    void testSelectBuilderWithSelectAllConditionWithoutPlaceholders() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1")).withSelectAllCondition(COLUMN3.isNull()).buildSelectAll();
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column3` IS NULL");
    }

    @Test
    void testSelectBuilderWithSelectOneEqualsCondition() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1")).withSelectOneEqualsCondition(COLUMN3).buildSelectOne();
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column3` = :column3");
    }

    @Test
    void testSelectBuilderWithSelectAllEqualsCondition() throws SQLException
    {
        var dataSource = getDataSourceMock();

        replayAll();
        var select = selectBuilder(TABLE, dataSource, (rs, rowNum) -> rs.getString("column1")).withSelectAllEqualsCondition(COLUMN3).buildSelectAll();
        verifyAll();

        assertThat(select.getSql()).isEqualTo("SELECT * FROM `schema`.`table` WHERE `schema`.`table`.`column3` = :column3");
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
