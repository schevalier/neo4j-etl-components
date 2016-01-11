package org.neo4j.ingest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.Formatting;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class HeaderFile
{
    private final Path directory;
    private final Formatting formatting;

    public HeaderFile( Path directory, Formatting formatting )
    {
        this.directory = directory;
        this.formatting = formatting;
    }

    public Path create( Collection<Field> fields, String filenamePrefix ) throws IOException
    {
        String headers = stringList(
                fields,
                formatting.delimiter().value(),
                Field::value ).toString();

        Path headerFile = directory.resolve( format( "%s_headers.csv", filenamePrefix ) );
        Files.write( headerFile, headers.getBytes() );

        return headerFile;
    }
}
