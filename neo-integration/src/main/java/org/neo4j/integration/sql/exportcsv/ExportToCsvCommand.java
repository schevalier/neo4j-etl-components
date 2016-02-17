package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.util.OperatingSystem;

public class ExportToCsvCommand
{
    private final ExportToCsvConfig config;
    private final DatabaseExportProvider databaseExportProvider;

    public ExportToCsvCommand( ExportToCsvConfig config, DatabaseExportProvider databaseExportProvider )
    {
        this.config = config;
        this.databaseExportProvider = databaseExportProvider;
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

        Collection<ExportToCsvResult> results = new ArrayList<>();

        try ( DatabaseClient databaseClient = new DatabaseClient( config.connectionConfig() ) )
        {
            HeaderFileWriter headerFileWriter = new HeaderFileWriter( config.destination(), config.formatting() );
            CsvFileWriter csvFileWriter = databaseExportProvider.createExportFileWriter( config, databaseClient );

            for ( DatabaseObject databaseObject : config.databaseObjects() )
            {
                results.add(
                        databaseExportProvider.exportDatabaseObject(
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
