package de.jaggl.sqlbuilder.springjdbc.readme;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Person
{
    private long id;
    private String forename;
    private String lastname;
}
