package de.jaggl.sqlbuilder.springjdbc.readme;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import de.jaggl.sqlbuilder.columns.number.integer.BigIntColumn;
import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleDeleteOne;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleInsert;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleSelectAll;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleSelectOne;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleUpdate;

@Repository
public class PersonRepositoryWithSimpleOperations
{
    public static final Table PERSONS = Table.create("persons");

    public static final BigIntColumn ID = PERSONS.bigIntColumn("id").autoIncrement().build();
    public static final VarCharColumn FORENAME = PERSONS.varCharColumn("forename").build();
    public static final VarCharColumn LASTNAME = PERSONS.varCharColumn("lastname").build();

    private SimpleInsert<Person> insert;
    private SimpleUpdate<Person> update;
    private SimpleDeleteOne deleteOne;
    private SimpleSelectOne<Person> selectOne;
    private SimpleSelectAll<Person> selectAll;

    public PersonRepositoryWithSimpleOperations(DataSource dataSource, PersonMapper personMapper)
    {
        insert = SimpleOperations.insert(PERSONS, dataSource, personMapper);
        update = SimpleOperations.update(PERSONS, dataSource, personMapper);
        deleteOne = SimpleOperations.deleteOne(PERSONS, dataSource);
        selectOne = SimpleOperations.selectOne(PERSONS, dataSource, personMapper);
        selectAll = SimpleOperations.selectAll(PERSONS, dataSource, personMapper);
    }

    public long insert(Person person)
    {
        return insert.execute(person);
    }

    public long update(Person person)
    {
        return update.execute(person);
    }

    public long delete(long personId)
    {
        return deleteOne.execute(personId);
    }

    public List<Person> getAll()
    {
        return selectAll.execute();
    }

    public Optional<Person> getById(long id)
    {
        return selectOne.execute(id);
    }
}
