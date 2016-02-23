package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
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

    public ExportToCsvResults execute() throws Exception
    {
        if ( Files.notExists( config.destination() ) )
        {
            Files.createDirectories( config.destination() );
        }

        if ( !OperatingSystem.isWindows() )
        {
            Commands.commands( "chmod", "0777", config.destination().toString() ).execute().await();
        }

        Collection<ExportToCsvResults.ExportToCsvResult> results = new ArrayList<>();

        try ( DatabaseClient databaseClient = new DatabaseClient( config.connectionConfig() ) )
        {
            HeaderFileWriter headerFileWriter = new HeaderFileWriter( config.destination(), config.formatting() );
            CsvFileWriter csvFileWriter = databaseExportService.createExportFileWriter( config, databaseClient );

            for ( DatabaseObject databaseObject : config.databaseObjects() )
            {
                results.add(
                        databaseExportService.exportDatabaseObjectToCsv(
                                databaseObject,
                                headerFileWriter,
                                csvFileWriter,
                                config
                        ) );
            }
        }

        return new ExportToCsvResults( results );
    }
}
