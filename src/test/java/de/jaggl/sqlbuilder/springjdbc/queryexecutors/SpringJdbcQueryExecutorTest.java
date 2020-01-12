package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import static de.jaggl.sqlbuilder.queries.Queries.insertInto;
import static de.jaggl.sqlbuilder.queries.Queries.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.dialect.Dialects;
import de.jaggl.sqlbuilder.queryexecutor.QueryExecutor;
import de.jaggl.sqlbuilder.schema.Table;

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
    void testWithInsert()
    {
        queryExecutor = new SpringJdbcQueryExecutor(jdbcTemplate, "MYSQL");

        jdbcTemplate.update(anyObject(PreparedStatementCreator.class), anyObject(KeyHolder.class));
        expectLastCall().andAnswer(() ->
        {
            var creator = (InsertPreparedStatementCreator) getCurrentArguments()[0];
            assertThat(creator.sql).isEqualTo("INSERT INTO `table` SET `table`.`column` = 'anyValue'");
            var keyHolder = (GeneratedKeyHolder) getCurrentArguments()[1];
            keyHolder.getKeyList().add(Map.of("anyName", Long.valueOf(5)));
            return Integer.valueOf(1);
        });

        replayAll();
        var result = insertInto(TABLE).set(COLUMN, "anyValue").executeAndReturnKey(queryExecutor);
        verifyAll();

        assertThat(result).isEqualTo(5);
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
