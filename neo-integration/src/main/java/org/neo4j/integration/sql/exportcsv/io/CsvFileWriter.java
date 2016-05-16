package org.neo4j.integration.sql.exportcsv.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import org.neo4j.integration.sql.MySqlDatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class CsvFileWriter
{
    private final ExportToCsvConfig config;
    private final MySqlDatabaseClient databaseClient;
    private final ResultsToFileWriter resultsToFileWriter;

    public CsvFileWriter( ExportToCsvConfig config, MySqlDatabaseClient databaseClient )
    {
        this.config = config;
        this.databaseClient = databaseClient;
        resultsToFileWriter = new ResultsToFileWriter(config.formatting());
    }

    public Path writeExportFile( CsvResource resource ) throws Exception
    {
        Loggers.Default.log( Level.INFO, format( "Writing data for %s", resource.name() ) );

        Path exportFile = createExportFile( resource.name() );

        try ( QueryResults results = executeSql( resource.sql() ) )
        {
            resultsToFileWriter.write( results, exportFile, resource );
        }

        return exportFile;
    }

    private Path createExportFile( String filenamePrefix ) throws IOException
    {
        Path exportFile = config.destination().resolve( format( "%s.csv", filenamePrefix ) );
        Files.createFile( exportFile );

        return exportFile;
    }

    private QueryResults executeSql( String sql ) throws Exception
    {
        return databaseClient.executeQuery( sql ).await();
    }
}
