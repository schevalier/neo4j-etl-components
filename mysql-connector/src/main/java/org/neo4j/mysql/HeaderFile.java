package org.neo4j.mysql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.ingest.config.Field;
import org.neo4j.mysql.config.ExportProperties;
import org.neo4j.mysql.config.Table;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class HeaderFile
{
    private final ExportProperties properties;

    public HeaderFile( ExportProperties properties )
    {
        this.properties = properties;
    }

    public Path create(Table table, String exportId) throws IOException
    {
        String headers = stringList(
                table.fieldMappings(),
                properties.formatting().delimiter().value(),
                Field::value ).toString();

        Path headerFile = properties.destination().resolve( format( "%s_headers", exportId ) );
        Files.write( headerFile, headers.getBytes() );

        return headerFile;
    }
}
