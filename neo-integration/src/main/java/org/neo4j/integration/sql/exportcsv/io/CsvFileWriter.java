package org.neo4j.integration.sql.exportcsv.io;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.sql.Results;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;

import static java.lang.String.format;

public class CsvFileWriter
{
    private final ExportToCsvConfig config;
    private final SqlRunner sqlRunner;

    public CsvFileWriter( ExportToCsvConfig config, SqlRunner sqlRunner )
    {
        this.config = config;
        this.sqlRunner = sqlRunner;
    }

    public Path writeExportFile( ColumnToCsvFieldMappings mappings,
                                 ExportSqlSupplier sqlSupplier,
                                 String filenamePrefix ) throws Exception
    {
        Path exportFile = config.destination().resolve( format( "%s.csv", filenamePrefix ) );
        Results results = sqlRunner.execute( sqlSupplier.sql( mappings, exportFile ) ).await();

        Column[] columns = mappings.columns().toArray( new Column[mappings.columns().size()] );
        int maxIndex = columns.length - 1;

        Files.createFile( exportFile );

        try ( BufferedWriter writer = Files.newBufferedWriter( exportFile, Charset.forName( "UTF8" ) ) )
        {
            while ( results.next() )
            {
                for ( int i = 0; i < maxIndex; i++ )
                {
                    writer.write( results.getString( columns[i].alias() ) );
                    writer.write( config.formatting().delimiter().value() );
                }
                writer.write( results.getString( columns[(maxIndex)].alias() ) );
                writer.newLine();
            }
        }

        return exportFile;
    }
}
