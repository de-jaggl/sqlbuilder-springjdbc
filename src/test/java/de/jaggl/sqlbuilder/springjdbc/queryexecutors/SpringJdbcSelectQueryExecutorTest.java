package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import static de.jaggl.sqlbuilder.queries.Queries.select;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.queryexecutor.QueryExecutor;
import de.jaggl.sqlbuilder.queryexecutor.RowExtractor;
import de.jaggl.sqlbuilder.schema.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

class SpringJdbcSelectQueryExecutorTest
{
    private static final Table TABLE = Table.create("table");

    private static final VarCharColumn COLUMN = TABLE.varCharColumn("column").build();

    private QueryExecutor queryExecutor;
    private JdbcTemplate jdbcTemplate;

    public SpringJdbcSelectQueryExecutorTest()
    {
        jdbcTemplate = createStrictMock(JdbcTemplate.class);
        queryExecutor = new SpringJdbcQueryExecutor(jdbcTemplate);
    }

    @Test
    void testQuery()
    {
        expect(jdbcTemplate.query(eq("SELECT `table`.`column` FROM `table`"), rowMapper(MyClass.class)))
                .andReturn(List.of(new MyClass("anyValue1"), new MyClass("anyValue2"), new MyClass("anyValue3")));

        replayAll();
        var result = select(COLUMN).from(TABLE).query(queryExecutor.select(new MyClassRowMapper()));
        verifyAll();

        assertThat(result).containsExactly(new MyClass("anyValue1"), new MyClass("anyValue2"), new MyClass("anyValue3"));
    }

    @Test
    void testQueryWithElementType()
    {
        expect(jdbcTemplate.queryForList(eq("SELECT `table`.`column` FROM `table`"), same(String.class)))
                .andReturn(List.of("anyValue1", "anyValue2", "anyValue3"));

        replayAll();
        var result = select(COLUMN).from(TABLE).query(queryExecutor.select(String.class));
        verifyAll();

        assertThat(result).containsExactly("anyValue1", "anyValue2", "anyValue3");
    }

    @Test
    void testQueryOne()
    {
        expect(jdbcTemplate.query(eq("SELECT `table`.`column` FROM `table`"), rowMapper(MyClass.class))).andReturn(List.of(new MyClass("anyValue")));

        replayAll();
        var result = select(COLUMN).from(TABLE).queryOne(queryExecutor.select(new MyClassRowMapper()));
        verifyAll();

        assertThat(result).contains(new MyClass("anyValue"));
    }

    @Test
    void testQueryOneNotFound()
    {
        expect(jdbcTemplate.query(eq("SELECT `table`.`column` FROM `table`"), rowMapper(MyClass.class))).andReturn(List.of());

        replayAll();
        var result = select(COLUMN).from(TABLE).queryOne(queryExecutor.select(new MyClassRowMapper()));
        verifyAll();

        assertThat(result).isEmpty();
    }

    @Test
    void testQueryOneMultipleFound()
    {
        expect(jdbcTemplate.query(eq("SELECT `table`.`column` FROM `table`"), rowMapper(MyClass.class)))
                .andReturn(List.of(new MyClass("anyValue1"), new MyClass("anyValue2"), new MyClass("anyValue3")));

        replayAll();
        assertThatThrownBy(() -> select(COLUMN).from(TABLE).queryOne(queryExecutor.select(new MyClassRowMapper())))
                .isInstanceOf(IncorrectResultSizeDataAccessException.class)
                .hasMessage("Incorrect result size: expected 1, actual 3");
        verifyAll();
    }

    @Test
    void testQueryOneWithElementType()
    {
        expect(jdbcTemplate.queryForList(eq("SELECT `table`.`column` FROM `table`"), same(String.class))).andReturn(List.of("anyValue"));

        replayAll();
        var result = select(COLUMN).from(TABLE).queryOne(queryExecutor.select(String.class));
        verifyAll();

        assertThat(result).contains("anyValue");
    }

    @Test
    void testQueryOneWithElementTypeNotFound()
    {
        expect(jdbcTemplate.queryForList(eq("SELECT `table`.`column` FROM `table`"), same(String.class))).andReturn(List.of());

        replayAll();
        var result = select(COLUMN).from(TABLE).queryOne(queryExecutor.select(String.class));
        verifyAll();

        assertThat(result).isEmpty();
    }

    @Test
    void testQueryOneWithElementTypeMultipleFound()
    {
        expect(jdbcTemplate.queryForList(eq("SELECT `table`.`column` FROM `table`"), same(String.class)))
                .andReturn(List.of("anyValue1", "anyValue2", "anyValue3"));

        replayAll();
        assertThatThrownBy(() -> select(COLUMN).from(TABLE).queryOne(queryExecutor.select(String.class)))
                .isInstanceOf(IncorrectResultSizeDataAccessException.class)
                .hasMessage("Incorrect result size: expected 1, actual 3");
        verifyAll();
    }

    @Test
    void testQueryExactOne()
    {
        expect(jdbcTemplate.queryForObject(eq("SELECT `table`.`column` FROM `table`"), rowMapper(MyClass.class))).andReturn(new MyClass("anyValue"));

        replayAll();
        var result = select(COLUMN).from(TABLE).queryExactOne(queryExecutor.select(new MyClassRowMapper()));
        verifyAll();

        assertThat(result).isEqualTo(new MyClass("anyValue"));
    }

    @Test
    void testQueryExactOneWithElementType()
    {
        expect(jdbcTemplate.queryForObject(eq("SELECT `table`.`column` FROM `table`"), same(String.class))).andReturn("anyValue");

        replayAll();
        var result = select(COLUMN).from(TABLE).queryExactOne(queryExecutor.select(String.class));
        verifyAll();

        assertThat(result).isEqualTo("anyValue");
    }

    private class MyClassRowMapper implements RowExtractor<MyClass>
    {
        @Override
        public MyClass mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            return new MyClass(rs.getString("column"));
        }
    }

    private static <T> RowMapper<T> rowMapper(@SuppressWarnings("unused") Class<T> elementType)
    {
        return anyObject(RowMapper.class);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    private class MyClass
    {
        private String value;
    }
}
