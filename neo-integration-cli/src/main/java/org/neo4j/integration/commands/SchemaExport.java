package org.neo4j.integration.commands;

import java.util.Collection;

import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class SchemaExport
{
    private final Collection<Table> startTable;
    private final Collection<Table> endTable;
    private final Collection<Join> joins;
    private final Collection<JoinTable> joinTables;

    public SchemaExport( Collection<Table> startTable,
                         Collection<Table> endTable,
                         Collection<Join> joins,
                         Collection<JoinTable> joinTables )
    {
        this.startTable = startTable;
        this.endTable = endTable;
        this.joins = joins;
        this.joinTables = joinTables;
    }

    public Collection<Table> startTable()
    {
        return startTable;
    }

    public Collection<Table> endTable()
    {
        return endTable;
    }

    public Collection<Join> joins()
    {
        return joins;
    }

    public Collection<JoinTable> joinTables()
    {
        return joinTables;
    }
}
