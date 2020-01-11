package de.jaggl.sqlbuilder.springjdbc.builders;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.sql.DataSource;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.Insert;

public class InsertFromInsertBuilder extends UpdateFromQueryBuilder
{
    private final Insert insert;

    InsertFromInsertBuilder(Insert insert, DataSource dataSource, Dialect dialect)
    {
        super(dataSource, dialect);
        this.insert = insert;
    }

    @Override
    protected String getSql(Dialect dialect)
    {
        return insert.build(dialect);
    }

    @Override
    protected List<Integer> getPlaceholderSqlTypes()
    {
        return insert.getValues().keySet().stream().map(column -> Integer.valueOf(column.getSqlType())).collect(toList());
    }
}
