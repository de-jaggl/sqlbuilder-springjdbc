package de.jaggl.sqlbuilder.springjdbc.builders;

import static java.util.Collections.emptyList;

import java.util.List;

import javax.sql.DataSource;

import de.jaggl.sqlbuilder.dialect.Dialect;
import de.jaggl.sqlbuilder.queries.Update;

/**
 * @author Martin Schumacher
 *
 * @since 1.0.0
 */
public class UpdateFromUpdateBuilder extends UpdateFromQueryBuilder
{
    private final Update update;

    UpdateFromUpdateBuilder(Update update, DataSource dataSource, Dialect dialect)
    {
        super(dataSource, dialect);
        this.update = update;
    }

    @Override
    protected String getSql(Dialect dialect)
    {
        return update.build(dialect);
    }

    @Override
    protected List<Integer> getPlaceholderSqlTypes()
    {
        return update.getWhere() != null ? update.getWhere().getPlaceholderSqlTypes() : emptyList();
    }
}
