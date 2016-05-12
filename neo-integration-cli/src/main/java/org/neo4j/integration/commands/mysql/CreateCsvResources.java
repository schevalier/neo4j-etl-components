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
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameResolver;

public class CreateCsvResources implements Callable<CsvResources>
{
    public static Callable<CsvResources> load( String uri )
    {
        return () ->
        {
            JsonNode root = new ObjectMapper().readTree( Paths.get( uri ).toFile() );
            return CsvResources.fromJson( root );
        };
    }

    public static Callable<CsvResources> load( Reader reader ) throws IOException
    {
        JsonNode root = new ObjectMapper().readTree( reader );
        return () -> CsvResources.fromJson( root );
    }

    public interface Events
    {
        Events EMPTY = new Events()
        {
            @Override
            public void onCreatingCsvResourcesFile()
            {
                // Do nothing
            }

            @Override
            public void onCsvResourcesCreated()
            {
                // Do nothing
            }
        };

        void onCreatingCsvResourcesFile();

        void onCsvResourcesCreated();
    }

    private final Events events;
    private final OutputStream output;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;
    private RelationshipNameResolver relationshipNameResolver;

    public CreateCsvResources( OutputStream output,
                               ConnectionConfig connectionConfig,
                               Formatting formatting,
                               DatabaseExportSqlSupplier sqlSupplier )
    {
        this( Events.EMPTY, output, connectionConfig, formatting, sqlSupplier, false );
    }

    public CreateCsvResources( Events events,
                               OutputStream output,
                               ConnectionConfig connectionConfig,
                               Formatting formatting,
                               DatabaseExportSqlSupplier sqlSupplier,
                               boolean columnNameAsRelationshipName )
    {
        this.events = events;
        this.output = output;
        this.connectionConfig = connectionConfig;
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
        this.relationshipNameResolver = new RelationshipNameResolver( columnNameAsRelationshipName );
    }

    @Override
    public CsvResources call() throws Exception
    {
        events.onCreatingCsvResourcesFile();

        SchemaExport schemaExport = new DatabaseInspector( new DatabaseClient( connectionConfig ) ).buildSchemaExport();
        CsvResources csvResources = schemaExport.createCsvResources( formatting, sqlSupplier,
                relationshipNameResolver );

        try ( Writer writer = new OutputStreamWriter( output ) )
        {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            writer.write( objectWriter.writeValueAsString( csvResources.toJson() ) );
        }

        events.onCsvResourcesCreated();

        return csvResources;
    }
}
