package org.neo4j.integration.commands;

import java.util.Collection;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinTableMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;
import org.neo4j.integration.util.Supplier;

import static java.util.Collections.emptyList;

public class SchemaExportService
{
    public SchemaExport doExport( SchemaDetails schemaDetails, Supplier<DatabaseClient> supplier ) throws Exception
    {
        String database = schemaDetails.database();

        TableName start = new TableName( database, schemaDetails.startTable() );
        TableName end = new TableName( database, schemaDetails.endTable() );

        try ( DatabaseClient databaseClient = supplier.supply() )
        {
            TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

            Collection<Table> startTable = tableMetadataProducer.createMetadataFor( start );
            Collection<Table> endTable = tableMetadataProducer.createMetadataFor( end );

            Collection<Join> joins = emptyList();
            Collection<JoinTable> joinTables = emptyList();

            if ( schemaDetails.joinTable().isPresent() )
            {
                joinTables = new JoinTableMetadataProducer( databaseClient )
                        .createMetadataFor(
                                new JoinTableInfo( new TableName( database, schemaDetails.joinTable().get() ),
                                        new TableNamePair( start, end ) ) );
            }
            else
            {
                joins = new JoinMetadataProducer( databaseClient )
                                .createMetadataFor( new TableNamePair( start, end ) );
            }
            return new SchemaExport( startTable, endTable, joins, joinTables );
        }
    }
}
