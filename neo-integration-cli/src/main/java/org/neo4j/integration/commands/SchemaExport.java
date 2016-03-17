package org.neo4j.integration.commands;

import java.util.Collection;

import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class SchemaExport
{
    private final Collection<Join> joins;
    private final Collection<JoinTable> joinTables;
    private final Collection<Table> tables;

    public SchemaExport( Collection<Table> tables, Collection<Join> joins, Collection<JoinTable> joinTables )
    {

        this.joins = joins;
        this.joinTables = joinTables;
        this.tables = tables;
    }

    public Collection<Table> tables()
    {
        return tables;
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
