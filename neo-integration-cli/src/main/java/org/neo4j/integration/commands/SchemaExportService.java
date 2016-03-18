package org.neo4j.integration.commands;

import java.util.ArrayList;
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

        TableName tableOne = new TableName( database, schemaDetails.tableOne() );
        TableName tableTwo = new TableName( database, schemaDetails.tableTwo() );

        try ( DatabaseClient databaseClient = supplier.supply() )
        {
            TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

            Collection<Table> tables = new ArrayList<>();
            tables.addAll( tableMetadataProducer.createMetadataFor( tableOne ) );
            tables.addAll( tableMetadataProducer.createMetadataFor( tableTwo ) );

            Collection<Join> joins = emptyList();
            Collection<JoinTable> joinTables = emptyList();

            if ( schemaDetails.joinTable().isPresent() )
            {
                joinTables = new JoinTableMetadataProducer( databaseClient )
                        .createMetadataFor(
                                new JoinTableInfo( new TableName( database, schemaDetails.joinTable().get() ),
                                        new TableNamePair( tableOne, tableTwo ) ) );
            }
            else
            {
                joins = new JoinMetadataProducer( databaseClient )
                                .createMetadataFor( new TableNamePair( tableOne, tableTwo ) );
            }
            return new SchemaExport( tables, joins, joinTables );
        }
    }
}
