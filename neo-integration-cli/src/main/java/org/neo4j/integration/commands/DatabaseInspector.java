package org.neo4j.integration.commands;

import java.util.Collection;
import java.util.HashSet;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableInfo;
import org.neo4j.integration.sql.metadata.TableInfoAssembler;
import org.neo4j.integration.sql.metadata.TableName;

public class DatabaseInspector
{
    private final String tablesToExclude;
    private final DatabaseClient databaseClient;
    private final TableInfoAssembler tableInfoAssembler;

    public DatabaseInspector( DatabaseClient databaseClient, String tablesToExclude )
    {
        this.databaseClient = databaseClient;
        this.tablesToExclude = tablesToExclude;
        this.tableInfoAssembler = new TableInfoAssembler( databaseClient, tablesToExclude );
    }

    public SchemaExport buildSchemaExport() throws Exception
    {
        HashSet<Table> tables = new HashSet<>();
        HashSet<Join> joins = new HashSet<>();
        HashSet<JoinTable> joinTables = new HashSet<>();

        for ( TableName tableName : databaseClient.tableNames() )
        {
            if( ! tableName.simpleName().equalsIgnoreCase( tablesToExclude ) )
            {
                buildSchema( tableName, tables, joins, joinTables );
            }
        }

        return new SchemaExport( tables, joins, joinTables );
    }

    private void buildSchema( TableName tableName,
                              Collection<Table> tables,
                              Collection<Join> joins,
                              Collection<JoinTable> joinTables ) throws Exception
    {
        TableInfo tableInfo = tableInfoAssembler.createTableInfo( tableName );

        if ( tableInfo.representsJoinTable() )
        {
            joinTables.add( tableInfo.createJoinTable() );
        }
        else
        {
            tables.add( tableInfo.createTable() );
            joins.addAll( tableInfo.createJoins() );
        }
    }
}
