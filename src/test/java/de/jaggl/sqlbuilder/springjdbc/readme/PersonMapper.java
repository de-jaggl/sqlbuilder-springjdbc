package de.jaggl.sqlbuilder.springjdbc.readme;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.jaggl.sqlbuilder.springjdbc.builders.utils.KeySetter;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.RowMapperAndParamSource;

@Component
public class PersonMapper implements RowMapperAndParamSource<Person>, KeySetter<Person>
{
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        return new Person(rs.getLong("id"), rs.getString("forename"), rs.getString("lastname"));
    }

    @Override
    public Map<String, Object> getParams(Person person)
    {
        return Map.of("id", Long.valueOf(person.getId()), "forename", person.getForename(), "lastname", person.getLastname());
    }

    @Override
    public void setKey(Person person, long key)
    {
        person.setId(key);
    }
}
