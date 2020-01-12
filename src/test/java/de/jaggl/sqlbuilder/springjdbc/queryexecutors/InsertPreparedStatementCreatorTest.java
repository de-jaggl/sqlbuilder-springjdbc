package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;

public class InsertPreparedStatementCreatorTest
{
    @Test
    public void testCreatePreparedStatement() throws SQLException
    {
        try (var connection = createStrictMock(Connection.class); var statement = createStrictMock(PreparedStatement.class))
        {
            expect(connection.prepareStatement("anySql")).andReturn(statement);
            statement.close();
            connection.close();

            replayAll();
            assertThat(new InsertPreparedStatementCreator("anySql").createPreparedStatement(connection)).isSameAs(statement);
        }
        verifyAll();
    }
}
