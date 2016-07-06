package org.neo4j.integration.sql.exportcsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMapping;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.SqlDataType;

class ResultsToFileWriter
{
    private Formatting formatting;

    ResultsToFileWriter( Formatting formatting )
    {
        this.formatting = formatting;
    }

    public void write( QueryResults results, Path file, MetadataMapping resource ) throws Exception
    {
        RowStrategy rowStrategy = RowStrategy.select( resource.graphObjectType() );

        ColumnToCsvFieldMappings mappings = resource.mappings();
        Column[] columns = mappings.columns().toArray( new Column[mappings.columns().size()] );

        int maxIndex = columns.length - 1;
        int rowIndex = 0;

        try ( BufferedWriter writer = Files.newBufferedWriter( file, Charset.forName( "UTF8" ) ) )
        {
            while ( results.next() )
            {
                rowIndex += 1;

                if ( rowStrategy.isWriteableRow( results, rowIndex, columns ) )
                {
                    for ( int i = 0; i < maxIndex; i++ )
                    {

                        writeFieldValueAndDelimiter(
                                columns[i].selectFrom( results, rowIndex ),
                                writer,
                                columns[i].useQuotes() );
                    }

                    writeFieldValueAndNewLine(
                            columns[maxIndex].selectFrom( results, rowIndex ),
                            writer,
                            columns[maxIndex].useQuotes() );
                }
            }
        }
    }

    public String applyFilterStrategies( String value, SqlDataType sqlDataType )
    {
        if ( sqlDataType.equals( SqlDataType.TINYINT ) && SqlDataType.TINYINT.toNeo4jDataType().equals( Neo4jDataType
                .Boolean ) )
        {
            if ( Integer.parseInt( value ) == 0 )
            {
                value = "false";
            }
            else
            {
                value = "true";
            }
        }

        return value;
    }

    private void writeFieldValueAndDelimiter( String value,
                                              BufferedWriter writer,
                                              boolean useQuotes ) throws IOException
    {
        sanitiseAndWriteData( value, writer, useQuotes );
        writer.write( this.formatting.delimiter().value() );
    }

    private void writeFieldValueAndNewLine( String value,
                                            BufferedWriter writer,
                                            boolean useQuotes ) throws IOException
    {
        sanitiseAndWriteData( value, writer, useQuotes );
        writer.newLine();
    }

    private void sanitiseAndWriteData( String value,
                                       BufferedWriter writer,
                                       boolean useQuotes ) throws IOException
    {

        if ( StringUtils.isNotEmpty( value ) )
        {
            if ( useQuotes )
            {
                this.formatting.quote().writeEnquoted( value, writer );
            }
            else
            {
                writer.write( value );
            }
        }
    }
}
