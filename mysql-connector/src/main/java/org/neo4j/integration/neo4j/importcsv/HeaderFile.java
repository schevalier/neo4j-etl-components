package org.neo4j.integration.neo4j.importcsv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

import static java.lang.String.format;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class HeaderFile
{
    private final Path directory;
    private final Formatting formatting;

    public HeaderFile( Path directory, Formatting formatting )
    {
        this.directory = directory;
        this.formatting = formatting;
    }

    public Path createHeaderFile( Collection<CsvField> fields, String filenamePrefix ) throws IOException
    {
        String headers = stringList(
                fields,
                formatting.delimiter().value(),
                CsvField::value ).toString();

        Path headerFile = directory.resolve( format( "%s_headers.csv", filenamePrefix ) );
        Files.write( headerFile, headers.getBytes() );

        return headerFile;
    }
}
