package org.neo4j.integration.commands;

import java.util.Optional;

public class SchemaDetails
{
    private final String database;
    private final String startTable;
    private final String endTable;
    private final Optional<String> joinTable;

    public SchemaDetails( String database, String startTable, String endTable, Optional<String> joinTable )
    {
        this.database = database;
        this.startTable = startTable;
        this.endTable = endTable;
        this.joinTable = joinTable;
    }

    public String getDatabase()
    {
        return database;
    }

    public String getStartTable()
    {
        return startTable;
    }

    public String getEndTable()
    {
        return endTable;
    }

    public Optional<String> getJoinTable()
    {
        return joinTable;
    }
}
