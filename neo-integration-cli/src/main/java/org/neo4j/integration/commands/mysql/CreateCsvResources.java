package org.neo4j.integration.commands.mysql;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;
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
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;
import org.neo4j.integration.sql.exportcsv.mapping.FilterOptions;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameResolver;
import org.neo4j.integration.sql.metadata.SqlDataType;

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

    private final CreateCsvResourcesEvents events;
    private final OutputStream output;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;
    private final RelationshipNameResolver relationshipNameResolver;
    private final List<String> tablesToExclude;

    public CreateCsvResources( OutputStream output,
                               ConnectionConfig connectionConfig,
                               Formatting formatting,
                               DatabaseExportSqlSupplier sqlSupplier )
    {
        this( CreateCsvResourcesEvents.EMPTY,
                output,
                connectionConfig,
                formatting,
                sqlSupplier,
                FilterOptions.DEFAULT );
    }

    public CreateCsvResources( CreateCsvResourcesEvents events,
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
        this.tablesToExclude = filterOptions.tablesToExclude();
        this.relationshipNameResolver = new RelationshipNameResolver( filterOptions.relationshipNameFrom() );

        SqlDataType.TINYINT.setNeoDataType( filterOptions.tinyIntAs().neoDataType() );
    }

    @Override
    public CsvResources call() throws Exception
    {
        events.onCreatingCsvResourcesFile();

        SchemaExport schemaExport =
                new DatabaseInspector( new DatabaseClient( connectionConfig ), tablesToExclude ).buildSchemaExport();
        CsvResources csvResources =
                schemaExport.createCsvResources( formatting, sqlSupplier, relationshipNameResolver );

        try ( Writer writer = new OutputStreamWriter( output ) )
        {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            writer.write( objectWriter.writeValueAsString( csvResources.toJson() ) );
        }

        events.onCsvResourcesCreated();

        return csvResources;
    }
}
