package org.neo4j.integration.commands;

import java.util.Collection;

import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;

public class SchemaExport
{
    private final Collection<Table> startTable;
    private final Collection<Table> endTable;
    private final Collection<Join> joins;

    public SchemaExport( Collection<Table> startTable, Collection<Table> endTable, Collection<Join> joins )
    {
        this.startTable = startTable;
        this.endTable = endTable;
        this.joins = joins;
    }

    public Collection<Table> getStartTable()
    {
        return startTable;
    }

    public Collection<Table> getEndTable()
    {
        return endTable;
    }

    public Collection<Join> getJoins()
    {
        return joins;
    }
}
