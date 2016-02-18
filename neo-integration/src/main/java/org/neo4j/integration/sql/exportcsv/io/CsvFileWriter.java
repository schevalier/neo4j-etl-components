package org.neo4j.integration.sql.exportcsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;

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

    public Path writeExportFile( ColumnToCsvFieldMappings mappings,
                                 DatabaseExportSqlSupplier sqlSupplier,
                                 String filenamePrefix ) throws Exception
    {
        Path exportFile = createExportFile( filenamePrefix );
        QueryResults results = executeSql( sqlSupplier.sql( mappings ) );

        writeResultsToFile( results, exportFile, mappings );

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

    private void writeResultsToFile( QueryResults results, Path file, ColumnToCsvFieldMappings mappings ) throws Exception
    {
        Column[] columns = mappings.columns().toArray( new Column[mappings.columns().size()] );
        int maxIndex = columns.length - 1;

        try ( BufferedWriter writer = Files.newBufferedWriter( file, Charset.forName( "UTF8" ) ) )
        {
            while ( results.next() )
            {
                for ( int i = 0; i < maxIndex; i++ )
                {
                    writeFieldValueAndDelimiter( results.getString( columns[i].alias() ), writer );
                }

                writeFieldValueAndNewLine( results.getString( columns[(maxIndex)].alias() ), writer );
            }
        }
    }

    private void writeFieldValueAndNewLine( String string, BufferedWriter writer ) throws Exception
    {
        writer.write( string );
        writer.newLine();
    }

    private void writeFieldValueAndDelimiter( String string, BufferedWriter writer ) throws Exception
    {
        writer.write( string );
        writer.write( config.formatting().delimiter().value() );
    }
}
