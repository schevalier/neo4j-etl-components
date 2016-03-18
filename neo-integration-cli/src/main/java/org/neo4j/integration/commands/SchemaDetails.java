package org.neo4j.integration.commands;

import java.util.Optional;

public class SchemaDetails
{
    private final String database;
    private final String tableOne;
    private final String tableTwo;
    private final Optional<String> joinTable;

    public SchemaDetails( String database, String tableOne, String tableTwo, Optional<String> joinTable )
    {
        this.database = database;
        this.tableOne = tableOne;
        this.tableTwo = tableTwo;
        this.joinTable = joinTable;
    }

    public String database()
    {
        return database;
    }

    public String tableOne()
    {
        return tableOne;
    }

    public String tableTwo()
    {
        return tableTwo;
    }

    public Optional<String> joinTable()
    {
        return joinTable;
    }
}
