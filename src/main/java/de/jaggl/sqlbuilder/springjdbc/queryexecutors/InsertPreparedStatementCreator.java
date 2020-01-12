package de.jaggl.sqlbuilder.springjdbc.queryexecutors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InsertPreparedStatementCreator implements PreparedStatementCreator
{
    String sql;

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException
    {
        return con.prepareStatement(sql);
    }
}
