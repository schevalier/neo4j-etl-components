package org.neo4j.integration.commands;

import java.util.Collection;

import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

public class SchemaExportService
{
    public SchemaExport doExport( ConnectionConfig connectionConfig,
                                  SchemaDetails schemaDetails ) throws Exception
    {
        TableName start = new TableName( schemaDetails.getDatabase(), schemaDetails.getStartTable() );
        TableName end = new TableName( schemaDetails.getDatabase(), schemaDetails.getEndTable() );

        try ( DatabaseClient databaseClient = new DatabaseClient( connectionConfig ) )
        {
            TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

            Collection<Table> startTable = tableMetadataProducer.createMetadataFor( start );
            Collection<Table> endTable = tableMetadataProducer.createMetadataFor( end );

            if ( schemaDetails.getJoinTable().isPresent() )
            {
                throw new RuntimeException( "======= ROAD BLOCKED! FEATURE UNDER CONSTRUCTION - TAKE DETOUR =======" );
            }
            else
            {
                Collection<Join> joins =
                        new JoinMetadataProducer( databaseClient )
                                .createMetadataFor( new TableNamePair( start, end ) );
                return new SchemaExport( startTable, endTable, joins );
            }
        }
    }

}
