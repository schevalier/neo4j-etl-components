package org.neo4j.integration.commands.mysql;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

public class CreateCsvResources implements Callable<CsvResources>
{
    public static Callable<CsvResources> fromExistingFile( String uri )
    {
        return () ->
        {
            JsonNode root = new ObjectMapper().readTree( URI.create( uri ).toURL() );
            return CsvResources.fromJson( root );
        };
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
            public void onCsvResourcesFileCreated( Path csvResourcesFile )
            {
                // Do nothing
            }
        };

        void onCreatingCsvResourcesFile();

        void onCsvResourcesFileCreated( Path csvResourcesFile );
    }

    private final Events events;
    private final Path csvDirectory;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;

    public CreateCsvResources( Path csvDirectory,
                               ConnectionConfig connectionConfig,
                               Formatting formatting,
                               DatabaseExportSqlSupplier sqlSupplier )
    {
        this( Events.EMPTY, csvDirectory, connectionConfig, formatting, sqlSupplier );
    }

    public CreateCsvResources( Events events,
                               Path csvDirectory,
                               ConnectionConfig connectionConfig,
                               Formatting formatting,
                               DatabaseExportSqlSupplier sqlSupplier )
    {
        this.events = events;
        this.csvDirectory = csvDirectory;
        this.connectionConfig = connectionConfig;
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
    }

    @Override
    public CsvResources call() throws Exception
    {
        events.onCreatingCsvResourcesFile();

        SchemaExport schemaExport = new DatabaseInspector( new DatabaseClient( connectionConfig ) ).buildSchemaExport();
        CsvResources csvResources = schemaExport.createCsvResources( formatting, sqlSupplier );

        Path mappingsFile = csvDirectory.resolve( "mappings.json" );

        try ( Writer writer = new BufferedWriter(
                new OutputStreamWriter( new FileOutputStream( mappingsFile.toFile() ), StandardCharsets.UTF_8 ) ) )
        {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            writer.write( objectWriter.writeValueAsString( csvResources.toJson() ) );
        }

        events.onCsvResourcesFileCreated( mappingsFile );

        return csvResources;
    }
}
