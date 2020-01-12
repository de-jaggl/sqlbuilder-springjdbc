package de.jaggl.sqlbuilder.springjdbc.readme;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.jaggl.sqlbuilder.columns.number.integer.BigIntColumn;
import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.queries.Queries;
import de.jaggl.sqlbuilder.queryexecutor.QueryExecutor;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.queryexecutors.SpringJdbcQueryExecutor;

@Repository
public class PersonRepositoryWithQueryExecutor
{
    public static final Table PERSONS = Table.create("persons");

    public static final BigIntColumn ID = PERSONS.bigIntColumn("id").autoIncrement().build();
    public static final VarCharColumn FORENAME = PERSONS.varCharColumn("forename").build();
    public static final VarCharColumn LASTNAME = PERSONS.varCharColumn("lastname").build();

    @Autowired
    private PersonMapper personMapper;

    private QueryExecutor queryExecutor;

    public PersonRepositoryWithQueryExecutor(JdbcTemplate jdbcTemplate)
    {
        queryExecutor = new SpringJdbcQueryExecutor(jdbcTemplate);
    }

    public long insert(Person person)
    {
        return Queries.insertInto(PERSONS)
                .set(FORENAME, person.getForename())
                .set(LASTNAME, person.getLastname())
                .executeAndReturnKey(queryExecutor);
    }

    public long update(Person person)
    {
        return Queries.update(PERSONS)
                .set(FORENAME, person.getForename())
                .set(LASTNAME, person.getLastname())
                .execute(queryExecutor);
    }

    public long delete(long personId)
    {
        return Queries.deleteFrom(PERSONS)
                .where(ID.eq(personId))
                .execute(queryExecutor);
    }

    public List<Person> getAll()
    {
        return Queries.select()
                .from(PERSONS)
                .query(queryExecutor.select(personMapper));
    }

    public Optional<Person> getById(long id)
    {
        return Queries.select()
                .from(PERSONS)
                .where(ID.eq(id))
                .queryOne(queryExecutor.select(personMapper));
    }
}
