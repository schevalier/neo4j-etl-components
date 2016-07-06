package org.neo4j.integration.commands.mysql;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.neo4j.integration.commands.DatabaseInspector;
import org.neo4j.integration.commands.SchemaExport;
import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMappings;
import org.neo4j.integration.sql.exportcsv.mapping.ExclusionMode;
import org.neo4j.integration.sql.exportcsv.mapping.FilterOptions;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameResolver;
import org.neo4j.integration.sql.metadata.SqlDataType;

public class GenerateMetadataMapping implements Callable<MetadataMappings>
{
    public static Callable<MetadataMappings> load( String uri )
    {
        return () ->
        {
            JsonNode root = new ObjectMapper().readTree( Paths.get( uri ).toFile() );
            return MetadataMappings.fromJson( root );
        };
    }

    public static Callable<MetadataMappings> load( Reader reader ) throws IOException
    {
        JsonNode root = new ObjectMapper().readTree( reader );
        return () -> MetadataMappings.fromJson( root );
    }

    private final GenerateMetadataMappingEvents events;
    private final OutputStream output;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;
    private final RelationshipNameResolver relationshipNameResolver;
    private final FilterOptions filterOptions;

    public GenerateMetadataMapping( OutputStream output,
                                    ConnectionConfig connectionConfig,
                                    Formatting formatting,
                                    DatabaseExportSqlSupplier sqlSupplier )
    {
        this( GenerateMetadataMappingEvents.EMPTY,
                output,
                connectionConfig,
                formatting,
                sqlSupplier,
                FilterOptions.DEFAULT );
    }

    public GenerateMetadataMapping( GenerateMetadataMappingEvents events,
                                    OutputStream output,
                                    ConnectionConfig connectionConfig,
                                    Formatting formatting,
                                    DatabaseExportSqlSupplier sqlSupplier,
                                    FilterOptions filterOptions )
    {
        this.events = events;
        this.output = output;
        this.connectionConfig = connectionConfig;
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
        this.filterOptions = filterOptions;
        this.relationshipNameResolver = new RelationshipNameResolver( filterOptions.relationshipNameFrom() );

        SqlDataType.TINYINT.setNeoDataType( filterOptions.tinyIntAs().neoDataType() );
    }

    @Override
    public MetadataMappings call() throws Exception
    {
        events.onGeneratingMetadataMapping();

        DatabaseClient databaseClient = new DatabaseClient( connectionConfig );

        if( filterOptions.exclusionMode().equals( ExclusionMode.INCLUDE ))
        {
            filterOptions.invertTables( databaseClient.tableNames() );
        }

        SchemaExport schemaExport = new DatabaseInspector( databaseClient, filterOptions.tablesToExclude() ).buildSchemaExport();
        MetadataMappings metadataMappings = schemaExport.createCsvResources( formatting, sqlSupplier, relationshipNameResolver );

        try ( Writer writer = new OutputStreamWriter( output ) )
        {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            writer.write( objectWriter.writeValueAsString( metadataMappings.toJson() ) );
        }

        events.onMetadataMappingGenerated();

        return metadataMappings;
    }
}
