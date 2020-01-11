package de.jaggl.sqlbuilder.springjdbc.builders;

import java.util.List;

import javax.sql.DataSource;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.Delete;

public class DeleteFromDeleteBuilder extends UpdateFromQueryBuilder
{
    private final Delete delete;

    DeleteFromDeleteBuilder(Delete delete, DataSource dataSource, Dialect dialect)
    {
        super(dataSource, dialect);
        this.delete = delete;
    }

    @Override
    protected String getSql(Dialect dialect)
    {
        return delete.build(dialect);
    }

    @Override
    protected List<Integer> getPlaceholderSqlTypes()
    {
        return delete.getWhere() != null ? delete.getWhere().getPlaceholderSqlTypes() : null;
    }
}
