package de.jaggl.sqlbuilder.springjdbc.builders;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import de.jaggl.sqlbuilder.dialect.Dialect;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PROTECTED)
public abstract class UpdateFromQueryBuilder
{
    private final DataSource dataSource;
    private final Dialect dialect;

    @SuppressWarnings("hiding")
    protected abstract String getSql(Dialect dialect);

    protected abstract List<Integer> getPlaceholderSqlTypes();

    public SqlUpdate build()
    {
        var sqlUpdate = new SqlUpdate(dataSource, getSql(dialect));
        var placeholderSqlTypes = getPlaceholderSqlTypes();
        if (placeholderSqlTypes != null)
        {
            placeholderSqlTypes.forEach(placeholderSqlType -> sqlUpdate.declareParameter(new SqlParameter(placeholderSqlType.intValue())));
        }
        return sqlUpdate;
    }

    public SqlUpdate buildAndCompile()
    {
        var sqlQuery = build();
        sqlQuery.compile();
        return sqlQuery;
    }
}
