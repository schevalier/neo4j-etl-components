package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.services.csv.ExportToCsvServiceProvider;
import org.neo4j.integration.sql.exportcsv.services.graphconfig.GraphDataConfigServiceProvider;
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

    public GraphConfig execute() throws Exception
    {
        if ( Files.notExists( config.destination() ) )
        {
            Files.createDirectories( config.destination() );
        }

        if ( !OperatingSystem.isWindows() )
        {
            Commands.commands( "chmod", "0777", config.destination().toString() ).execute().await();
        }

        Collection<GraphDataConfig> results = new ArrayList<>();

        try ( DatabaseClient databaseClient = new DatabaseClient( config.connectionConfig() ) )
        {
            HeaderFileWriter headerFileWriter = new HeaderFileWriter( config.destination(), config.formatting() );
            CsvFileWriter csvFileWriter = databaseExportService.createExportFileWriter( config, databaseClient );

            ExportToCsvServiceProvider exportToCsvServiceProvider = new ExportToCsvServiceProvider();
            GraphDataConfigServiceProvider graphDataConfigServiceProvider = new GraphDataConfigServiceProvider();

            for ( DatabaseObject databaseObject : config.databaseObjects() )
            {
                Collection<Path> csvFiles = databaseObject.createService( exportToCsvServiceProvider )
                        .exportToCsv( databaseExportService.sqlSupplier(), headerFileWriter, csvFileWriter, config );
                GraphDataConfig graphDataConfig = databaseObject.createService( graphDataConfigServiceProvider )
                        .createGraphDataConfig( csvFiles );

                results.add( graphDataConfig );
            }
        }

        return new GraphConfig( results );
    }
}
