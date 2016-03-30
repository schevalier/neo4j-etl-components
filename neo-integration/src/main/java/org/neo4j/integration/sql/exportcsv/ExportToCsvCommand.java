package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Files;

import org.neo4j.integration.neo4j.importcsv.config.Manifest;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.mapping.Resource;
import org.neo4j.integration.sql.exportcsv.mapping.ResourceProvider;
import org.neo4j.integration.sql.exportcsv.services.ResourceToCsvFilesService;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.util.OperatingSystem;

public class ExportToCsvCommand
{
    private final ExportToCsvConfig config;
    private final DatabaseExportService databaseExportService;

    public ExportToCsvCommand( ExportToCsvConfig config, DatabaseExportService databaseExportService )
    {
        this.config = config;
        this.databaseExportService = databaseExportService;
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
            CsvFileWriter csvFileWriter = databaseExportService.createExportFileWriter( config, databaseClient );

            ResourceProvider resourceProvider =
                    new ResourceProvider( config.formatting(), databaseExportService.sqlSupplier() );
            ResourceToCsvFilesService exportService = new ResourceToCsvFilesService( headerFileWriter, csvFileWriter );

            for ( DatabaseObject databaseObject : config.databaseObjects() )
            {
                Resource resource = databaseObject.invoke( resourceProvider );

                manifest.add( exportService.exportToCsv( resource ) );
            }
        }

        return manifest;
    }
}
