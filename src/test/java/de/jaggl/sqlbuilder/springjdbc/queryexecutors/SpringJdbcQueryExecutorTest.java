package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import static de.jaggl.sqlbuilder.queries.Queries.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.dialect.Dialects;
import de.jaggl.sqlbuilder.queryexecutor.QueryExecutor;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.queryexecutors.SpringJdbcQueryExecutor;

class SpringJdbcQueryExecutorTest
{
    private static final Table TABLE = Table.create("table");

    private static final VarCharColumn COLUMN = TABLE.varCharColumn("column").build();

    private QueryExecutor queryExecutor;
    private JdbcTemplate jdbcTemplate;

    public SpringJdbcQueryExecutorTest()
    {
        jdbcTemplate = createStrictMock(JdbcTemplate.class);
    }

    @Test
    void testWithUpdatable()
    {
        queryExecutor = new SpringJdbcQueryExecutor(jdbcTemplate, "MYSQL");

        expect(Integer.valueOf(jdbcTemplate.update("UPDATE `table` SET `table`.`column` = 'anyValue'"))).andReturn(Integer.valueOf(3));

        replayAll();
        var result = update(TABLE).set(COLUMN, "anyValue").execute(queryExecutor);
        verifyAll();

        assertThat(result).isEqualTo(3);
    }

    @Test
    void testWithExecutable()
    {
        queryExecutor = new SpringJdbcQueryExecutor(jdbcTemplate, Dialects.MYSQL);

        jdbcTemplate.execute("CREATE TABLE `table` (`column` VARCHAR DEFAULT NULL)");

        replayAll();
        TABLE.buildCreateTable().execute(queryExecutor);
        verifyAll();
    }
}
