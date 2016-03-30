package org.neo4j.integration.sql.exportcsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.Resource;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class CsvFileWriter
{
    private final ExportToCsvConfig config;
    private final DatabaseClient databaseClient;

    public CsvFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient )
    {
        this.config = config;
        this.databaseClient = databaseClient;
    }

    public Path writeExportFile( Resource resource ) throws Exception
    {
        Loggers.Default.log( Level.INFO, format( "Writing data for %s", resource.name() ) );

        Path exportFile = createExportFile( resource.name() );
        QueryResults results = executeSql( resource.sql() );

        writeResultsToFile( results, exportFile, resource );

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

    private void writeResultsToFile( QueryResults results, Path file, Resource resource ) throws Exception
    {
        ColumnToCsvFieldMappings mappings = resource.mappings();
        Column[] columns = mappings.columns().toArray( new Column[mappings.columns().size()] );

        int maxIndex = columns.length - 1;

        try ( BufferedWriter writer = Files.newBufferedWriter( file, Charset.forName( "UTF8" ) ) )
        {
            while ( results.next() )
            {
                if ( resource.rowStrategy().test( results, mappings.columns() ) )
                {
                    for ( int i = 0; i < maxIndex; i++ )
                    {
                        writeFieldValueAndDelimiter( columns[i].selectFrom( results ), writer );
                    }

                    writeFieldValueAndNewLine( columns[maxIndex].selectFrom( results ), writer );
                }
            }
        }
    }

    private void writeFieldValueAndDelimiter( String value, BufferedWriter writer ) throws Exception
    {
        sanitiseAndWriteData( value, writer );
        writer.write( config.formatting().delimiter().value() );
    }

    private void writeFieldValueAndNewLine( String value, BufferedWriter writer ) throws Exception
    {
        sanitiseAndWriteData( value, writer );
        writer.newLine();
    }

    private void sanitiseAndWriteData( String value, BufferedWriter writer ) throws IOException
    {
        if ( StringUtils.isNotEmpty( value ) )
        {
            writer.write( value );
        }
    }
}
