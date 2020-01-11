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
  <version>1.1.0</version>
</dependency>
```

### Simple Example

```java
@Repository
public class PersonRepository
{
  private static final Table PERSONS = Table.create("persons");
  private static final BigIntColumn ID = PERSONS.bigIntColumn("id").autoIncrement().build();
  private static final VarCharColumn FORENAME = PERSONS.varCharColumn("forename").build();
  private static final VarCharColumn LASTNAME = PERSONS.varCharColumn("lastname").build();

  private SimpleJdbcInsert insert;
  private SqlQuery update;
  private SqlQuery delete;
  private SqlQuery selectAll;
  private SqlQuery selectOne;

  @Autowired
  private PersonMapper personMapper;

  public PersonRepository(DataSource dataSource, PersonMapper personMapper)
  {
    insert = SqlOperations.insert(PERSONS, dataSource);
    update = SqlOperations.update(PERSONS, dataSource);
    delete = SqlOperations.delete(PERSONS, dataSource);
    selectAll = SqlOperations.selectAll(PERSONS, dataSource, personMapper);
    selectOne = SqlOperations.selectOne(PERSONS, dataSource, personMapper);
  }

  public void insert(Person person)
  {
    person.setId(insert.executeAndReturnKey(personMapper.getParams(person)));
  }

  public void update(Person person)
  {
    update.updateByNamedParam(personMapper.getParams(person));
  }

  public void delete(long personId)
  {
    return delete.update(personId);
  }

  public List<Person> getAll()
  {
    return selectAll.execute();
  }

  public Person getById(long id)
  {
    return selectOne.execute(id);
  }
}
```
