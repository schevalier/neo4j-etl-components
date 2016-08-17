package org.neo4j.etl.sql.exportcsv;

import java.nio.file.Files;

import org.neo4j.etl.neo4j.importcsv.config.Manifest;
import org.neo4j.etl.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.etl.process.Commands;
import org.neo4j.etl.sql.DatabaseClient;
import org.neo4j.etl.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.etl.sql.exportcsv.io.TinyIntResolver;
import org.neo4j.etl.sql.exportcsv.mapping.MetadataMapping;
import org.neo4j.etl.sql.exportcsv.mapping.MetadataMappings;
import org.neo4j.etl.sql.exportcsv.services.ResourceToCsvFilesService;
import org.neo4j.etl.util.OperatingSystem;
import org.neo4j.etl.util.Preconditions;

public class ExportToCsvCommand
{
    private final ExportToCsvConfig config;
    private final MetadataMappings metadataMappings;
    private TinyIntResolver tinyIntResolver;

    public ExportToCsvCommand( ExportToCsvConfig config,
                               MetadataMappings metadataMappings,
                               TinyIntResolver tinyIntResolver )
    {
        this.config = Preconditions.requireNonNull( config, "ExportToCsvConfig" );
        this.metadataMappings = Preconditions.requireNonNull( metadataMappings, "MetadataMappings" );
        this.tinyIntResolver = tinyIntResolver;
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
            CsvFileWriter csvFileWriter = new CsvFileWriter( config, databaseClient, tinyIntResolver );
            ResourceToCsvFilesService exportService = new ResourceToCsvFilesService( headerFileWriter, csvFileWriter );

            for ( MetadataMapping resource : metadataMappings )
            {
                manifest.add( exportService.exportToCsv( resource ) );
            }
        }

        return manifest;
    }
}
