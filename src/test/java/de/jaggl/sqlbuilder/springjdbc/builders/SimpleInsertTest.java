package de.jaggl.sqlbuilder.springjdbc.builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import de.jaggl.sqlbuilder.springjdbc.builders.utils.ParamSource;

public class SimpleInsertTest
{
    @Test
    public void testWithoutConsideringKey()
    {
        var simpleJdbcInsert = createStrictMock(SimpleJdbcInsert.class);
        var paramSource = getParamSourceMock();

        var simpleInsert = new SimpleInsert<>(simpleJdbcInsert, paramSource, false);

        expect(paramSource.getParams("anyData")).andReturn(Map.of("name", "anyName"));
        expect(Integer.valueOf(simpleJdbcInsert.execute(Map.of("name", "anyName")))).andReturn(Integer.valueOf(5));

        replayAll();
        var result = simpleInsert.execute("anyData");
        verifyAll();

        assertThat(result).isEqualTo(5);
    }

    private static <T> ParamSource<T> getParamSourceMock()
    {
        return createStrictMock(ParamSource.class);
    }
}
