package org.neo4j.mysql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.ingest.config.Field;
import org.neo4j.mysql.config.ExportConfig;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class HeaderFile
{
    private final ExportConfig config;

    public HeaderFile( ExportConfig config )
    {
        this.config = config;
    }

    public Path create(String exportId) throws IOException
    {
        String headers = stringList(
                config.table().fieldMappings(),
                config.formatting().delimiter().value(),
                Field::value ).toString();

        Path headerFile = config.destination().resolve( format( "%s_headers", exportId ) );
        Files.write( headerFile, headers.getBytes() );

        return headerFile;
    }
}
