package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Files;

import org.neo4j.integration.neo4j.importcsv.config.Manifest;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMapping;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMappings;
import org.neo4j.integration.sql.exportcsv.services.ResourceToCsvFilesService;
import org.neo4j.integration.util.OperatingSystem;
import org.neo4j.integration.util.Preconditions;

public class ExportToCsvCommand
{
    private final ExportToCsvConfig config;
    private final MetadataMappings metadataMappings;

    public ExportToCsvCommand( ExportToCsvConfig config, MetadataMappings metadataMappings )
    {
        this.config = Preconditions.requireNonNull( config, "ExportToCsvConfig" );
        this.metadataMappings = Preconditions.requireNonNull( metadataMappings, "CsvResources" );
    }

    public Manifest execute() throws Exception
    {
        if ( Files.notExists( config.destination() ) )
        {
            Files.createDirectories( config.destination() );
        }

        if ( !OperatingSystem.isWindows() )
        {
            Commands.commands( "chmod", "0777", config.destination().toString() ).execute().await();
        }

        Manifest manifest = new Manifest();

        try ( DatabaseClient databaseClient = new DatabaseClient( config.connectionConfig() ) )
        {
            HeaderFileWriter headerFileWriter = new HeaderFileWriter( config.destination(), config.formatting() );
            CsvFileWriter csvFileWriter = new CsvFileWriter( config, databaseClient );
            ResourceToCsvFilesService exportService = new ResourceToCsvFilesService( headerFileWriter, csvFileWriter );

            for ( MetadataMapping resource : metadataMappings )
            {
                manifest.add( exportService.exportToCsv( resource ) );
            }
        }

        return manifest;
    }
}
