package org.neo4j.integration.neo4j;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statements
{
    @JsonProperty("statements")
    private List<Statement> statements = new ArrayList<>();

    public Statements add( Statement statement )
    {
        statements.add( statement );
        return this;
    }
}
