package org.neo4j.integration.sql.exportcsv;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinTableMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static java.lang.String.format;

public class DatabaseExport
{
    private final TableMetadataProducer tableMetadataProducer;
    private final JoinMetadataProducer joinMetadataProducer;
    private final JoinTableMetadataProducer joinTableMetadataProducer;
    private final DatabaseClient databaseClient;

    public DatabaseExport( TableMetadataProducer tableMetadataProducer,
                           JoinMetadataProducer joinMetadataProducer,
                           JoinTableMetadataProducer joinTableMetadataProducer,
                           DatabaseClient databaseClient )
    {
        this.tableMetadataProducer = tableMetadataProducer;
        this.joinMetadataProducer = joinMetadataProducer;
        this.joinTableMetadataProducer = joinTableMetadataProducer;
        this.databaseClient = databaseClient;
    }

    public void updateConfig( ExportToCsvConfig.Builder config, TableName tableName ) throws Exception
    {
        QueryResults results = databaseClient.executeQuery( listKeys( tableName ) ).await();

        Collection<String> primaryKeys = new HashSet<>();
        Collection<String> foreignKeys = new HashSet<>();

        Collection<Table> tables = new HashSet<>();
        Collection<Join> joins = new HashSet<>();
        Collection<JoinTable> joinTables = new HashSet<>();

        while ( results.next() )
        {
            String columnName = results.getString( "COLUMN_NAME" );
            String referencedTableName = results.getString( "REFERENCED_TABLE_NAME" );
            String columnKey = results.getString( "COLUMN_KEY" );

            if ( columnKey.equalsIgnoreCase( "PRI" ) && StringUtils.isEmpty( referencedTableName ) )
            {
                primaryKeys.add( columnName );
            }

            if ( columnKey.equalsIgnoreCase( "MUL" ) )
            {
                foreignKeys.add( referencedTableName );
            }
        }

        if ( primaryKeys.isEmpty() && foreignKeys.size() == 2 )
        {
            Iterator<String> iterator = foreignKeys.iterator();
            TableName start = new TableName( tableName.schema(), iterator.next() );
            TableName end = new TableName( tableName.schema(), iterator.next() );
            joinTables.addAll( joinTableMetadataProducer.createMetadataFor(
                    new JoinTableInfo( tableName, new TableNamePair( start, end ) ) ) );
        }
        else
        {
            tables.addAll( tableMetadataProducer.createMetadataFor( tableName ) );
            for ( String foreignKey : foreignKeys )
            {
                joins.addAll( joinMetadataProducer.createMetadataFor( new TableNamePair( tableName, new TableName(
                        tableName.schema(), foreignKey ) ) ) );
            }
        }

        config.addTables( tables );
        config.addJoins( joins );
        config.addJoinTables( joinTables );
    }

    private String listKeys( TableName tableName )
    {
        return format( "SELECT kcu.COLUMN_NAME, " +
                "      kcu.REFERENCED_TABLE_NAME, " +
                "      kcu.REFERENCED_COLUMN_NAME, " +
                "      c.COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu INNER JOIN INFORMATION_SCHEMA.COLUMNS c " +
                "ON kcu.COLUMN_NAME = c.COLUMN_NAME AND kcu.TABLE_NAME = c.TABLE_NAME " +
                "WHERE kcu.TABLE_SCHEMA = '%s' " +
                "AND kcu.TABLE_NAME = '%s' " +
                "ORDER BY kcu.COLUMN_NAME, kcu.REFERENCED_TABLE_NAME", tableName.schema(), tableName.simpleName() );
    }
}
