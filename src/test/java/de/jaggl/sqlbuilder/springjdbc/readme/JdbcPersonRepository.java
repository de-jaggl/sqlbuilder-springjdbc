package de.jaggl.sqlbuilder.springjdbc.readme;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import de.jaggl.sqlbuilder.columns.number.integer.BigIntColumn;
import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.repository.SimpleJdbcRepository;

@Repository
public class JdbcPersonRepository extends SimpleJdbcRepository<Person>
{
    public static final Table PERSONS = Table.create("persons");

    public static final BigIntColumn ID = PERSONS.bigIntColumn("id").autoIncrement().build();
    public static final VarCharColumn FORENAME = PERSONS.varCharColumn("forename").build();
    public static final VarCharColumn LASTNAME = PERSONS.varCharColumn("lastname").build();

    public JdbcPersonRepository(DataSource dataSource, PersonMapper personMapper)
    {
        super(PERSONS, dataSource, personMapper);
    }
}
