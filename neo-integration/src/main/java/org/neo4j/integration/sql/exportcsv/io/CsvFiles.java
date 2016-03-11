package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Path;
import java.util.Collection;

import static java.util.Arrays.asList;

public class CsvFiles
{
    private final Path header;
    private final Path body;

    public CsvFiles( Path header, Path body )
    {
        this.header = header;
        this.body = body;
    }

    public Path header()
    {
        return header;
    }

    public Path body()
    {
        return body;
    }

    public Collection<Path> asCollection()
    {
        return asList( header, body );
    }
}
