package org.neo4j.etl.neo4j.importcsv.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.etl.neo4j.importcsv.fields.CsvField;
import org.neo4j.etl.util.Loggers;

import static java.lang.String.format;

public class HeaderFileWriter
{
    private final Path directory;
    private final Formatting formatting;

    public HeaderFileWriter( Path directory, Formatting formatting )
    {
        this.directory = directory;
        this.formatting = formatting;
    }

    public Path writeHeaderFile( String description,
                                 Collection<CsvField> fields,
                                 String filenamePrefix ) throws IOException
    {
        Loggers.Default.log( Level.INFO,
                format( "Writing CSV headers for %s %s", description.toLowerCase(), filenamePrefix ) );
        String headers = fields.stream()
                .map( f -> f.value( formatting.propertyFormatter() ) )
                .collect( Collectors.joining( formatting.delimiter().value() ) );

        Path headerFile = directory.resolve( format( "%s_headers.csv", filenamePrefix ) );
        Files.write( headerFile, headers.getBytes() );

        return headerFile;
    }
}
