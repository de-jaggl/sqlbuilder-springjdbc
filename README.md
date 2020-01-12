# SQLbuilder <-> Spring JDBC

[![Maven Central](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/de/jaggl/sqlbuilder/sqlbuilder-springjdbc/maven-metadata.xml.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22de.jaggl.sqlbuilder%22%20AND%20a%3A%22sqlbuilder-springjdbc%22)
[![Release](https://github.com/de-jaggl/sqlbuilder-springjdbc/workflows/release/badge.svg)](https://github.com/de-jaggl/sqlbuilder-springjdbc/actions)
[![Nightly build](https://github.com/de-jaggl/sqlbuilder-springjdbc/workflows/nightly/badge.svg)](https://github.com/de-jaggl/sqlbuilder-springjdbc/actions)
[![javadoc](https://javadoc.io/badge2/de.jaggl.sqlbuilder/sqlbuilder-springjdbc/javadoc.svg?)](https://javadoc.io/doc/de.jaggl.sqlbuilder/sqlbuilder-springjdbc)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=alert_status)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=security_rating)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=ncloc)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=coverage)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=de-jaggl_sqlbuilder-springjdbc&metric=sqale_index)](https://sonarcloud.io/dashboard?id=de-jaggl_sqlbuilder-springjdbc)
[![GitHub](https://img.shields.io/github/license/de-jaggl/sqlbuilder-springjdbc)](https://github.com/de-jaggl/sqlbuilder-springjdbc/blob/master/LICENSE)
[![Gitter](https://badges.gitter.im/de-jaggl/community.svg)](https://gitter.im/de-jaggl/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

A Java-Library that offers some support for easy combining the SQLbuilder-Core with Spring JDBC

### Dependency

```xml
<dependency>
  <groupId>de.jaggl.sqlbuilder</groupId>
  <artifactId>sqlbuilder-springjdbc</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Simple Example

Consider haven the following domain-class Person (using the @Data-annotation from lombok, for a simpler, smaller example):
```java
@Data
@AllArgsConstructor
public class Person
{
    private long id;
    private String forename;
    private String lastname;
}
```

Then it is possible to implement a PersonMapper as follows:
```java
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
```

Now you can simply implement a repository like this:
```java
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
```

Another possibility is the following implementation:
```java
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
```

It is up to you, which implementation you use. The first example is the simpler one, which covers the default CRUD-methods and default select-methods. It assumes, that you are using a very standard domain-class with, for example, an identifier from type long. For more generic use, the second approach maybe the better one.