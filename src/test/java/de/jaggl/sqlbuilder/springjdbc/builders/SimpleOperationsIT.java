package de.jaggl.sqlbuilder.springjdbc.builders;

import static de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations.deleteOne;
import static de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations.insert;
import static de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations.selectAll;
import static de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations.selectOne;
import static de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperations.update;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import de.jaggl.sqlbuilder.columns.number.integer.BigIntColumn;
import de.jaggl.sqlbuilder.columns.string.VarCharColumn;
import de.jaggl.sqlbuilder.schema.Table;
import de.jaggl.sqlbuilder.springjdbc.builders.SimpleOperationsIT.TestConfiguration;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.KeySetter;
import de.jaggl.sqlbuilder.springjdbc.builders.utils.RowMapperAndParamSource;
import de.jaggl.sqlbuilder.springjdbc.queryexecutors.SpringJdbcQueryExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
class SimpleOperationsIT
{
    protected static final Table TABLE = Table.create("person");

    protected static final BigIntColumn ID = TABLE.bigIntColumn("id").unsigned().noDefault().autoIncrement().build();
    protected static final VarCharColumn FORENAME = TABLE.varCharColumn("forename").size(20).build();
    protected static final VarCharColumn LASTNAME = TABLE.varCharColumn("lastname").size(20).build();

    private SimpleInsert<Person> insert;
    private SimpleUpdate<Person> update;
    private SimpleDeleteOne deleteOne;

    private SimpleSelectOne<Person> selectOne;
    private SimpleSelectAll<Person> selectAll;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersonMapper personMapper;

    @BeforeEach
    void setUp()
    {
        insert = insert(TABLE, dataSource, personMapper);
        update = update(TABLE, dataSource, personMapper);
        deleteOne = deleteOne(TABLE, dataSource);

        selectOne = selectOne(TABLE, dataSource, personMapper);
        selectAll = selectAll(TABLE, dataSource, personMapper);
    }

    @Test
    void testAll()
    {
        TABLE.buildCreateTable().execute(new SpringJdbcQueryExecutor(new JdbcTemplate(dataSource)));

        assertThat(selectAll.execute()).isEmpty();

        var person = new Person(0, "Martin", "Schumacher");
        var result = insert.execute(person);
        assertThat(person.getId()).isPositive().isEqualTo(result);

        person = selectOne.execute(person.getId()).orElse(null);
        assertThat(person.getForename()).isEqualTo("Martin");
        assertThat(person.getLastname()).isEqualTo("Schumacher");

        assertThat(selectAll.execute()).hasSize(1);

        person.setForename("Jonas Martin");

        assertThat(update.execute(person)).isEqualTo(1);

        person = selectOne.execute(person.getId()).orElse(null);
        assertThat(person.getForename()).isEqualTo("Jonas Martin");
        assertThat(person.getLastname()).isEqualTo("Schumacher");

        assertThat(deleteOne.execute(person.getId())).isEqualTo(1);

        assertThat(selectOne.execute(person.getId())).isEmpty();

        assertThat(selectAll.execute()).isEmpty();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Person
    {
        private long id;
        private String forename;
        private String lastname;
    }

    public static class PersonMapper implements RowMapperAndParamSource<Person>, KeySetter<Person>
    {
        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            var person = new Person();
            person.setId(rs.getLong("id"));
            person.setForename(rs.getString("forename"));
            person.setLastname(rs.getString("lastname"));
            return person;
        }

        @Override
        public Map<String, Object> getParams(Person person)
        {
            var params = new HashMap<String, Object>();
            params.put("id", Long.valueOf(person.getId()));
            params.put("forename", person.getForename());
            params.put("lastname", person.getLastname());
            return params;
        }

        @Override
        public void setKey(Person person, long key)
        {
            person.setId(key);
        }
    }

    @Configuration
    public static class TestConfiguration
    {
        @Value("${embedded.container.mysql.url}")
        private String url;

        @Value("${embedded.container.mysql.root-password}")
        private String password;

        @Bean
        public DataSource dataSource()
        {
            return DataSourceBuilder.create()
                    .driverClassName("com.mysql.jdbc.Driver")
                    .url(url)
                    .username("root")
                    .password(password)
                    .build();
        }

        @Bean
        public PersonMapper personMapper()
        {
            return new PersonMapper();
        }
    }
}
