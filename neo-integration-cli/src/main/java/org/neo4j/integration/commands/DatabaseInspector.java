package org.neo4j.integration.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.MySqlDatabaseClient;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableInfo;
import org.neo4j.integration.sql.metadata.TableInfoAssembler;
import org.neo4j.integration.sql.metadata.TableName;

public class DatabaseInspector
{
    private final MySqlDatabaseClient databaseClient;

    public DatabaseInspector( MySqlDatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    public SchemaExport buildSchemaExport() throws Exception
    {
        HashSet<Table> tables = new HashSet<>();
        HashSet<Join> joins = new HashSet<>();
        HashSet<JoinTable> joinTables = new HashSet<>();

        for ( TableName tableName : databaseClient.tableNames() )
        {
            buildSchema( tableName, tables, joins, joinTables );
        }

        return new SchemaExport( tables, joins, joinTables );
    }

    private void buildSchema( TableName tableName,
                              Collection<Table> tables,
                              Collection<Join> joins,
                              Collection<JoinTable> joinTables ) throws Exception
    {
        TableInfo tableInfo = new TableInfoAssembler( databaseClient ).createTableInfo( tableName );

        Table.Builder tableBuilder = Table.builder().name( tableName );
        tableInfo.columnsLessKeys().forEach( tableBuilder::addColumn );

        if ( tableInfo.representsJoinTable() )
        {
            List<JoinKey> joinKeys = tableInfo.foreignKeys().stream()
                    .sorted( ( o1, o2 ) -> o1.sourceColumn().name().compareTo( o2.sourceColumn().name() ) )
                    .collect( Collectors.toList() );
            joinTables.add( new JoinTable( new Join( joinKeys.get( 0 ), joinKeys.get( 1 ) ), tableBuilder.build() ) );
        }
        else
        {
            Optional<Column> primaryKeyColumn = tableInfo.primaryKey();
            if ( primaryKeyColumn.isPresent() )
            {
                tableBuilder.addColumn( primaryKeyColumn.get() );
            }
            tables.add( tableBuilder.build() );

            for ( JoinKey joinKey : tableInfo.foreignKeys() )
            {
                if ( primaryKeyColumn.isPresent() )
                {
                    joins.add( new Join( new JoinKey( primaryKeyColumn.get(), primaryKeyColumn.get() ), joinKey ) );
                }
                else
                {
                    throw new IllegalStateException( "Unsupported: foreign key in a table that has no primary key, " +
                            "and which is not a join table." );
                }
            }
        }
    }
}
