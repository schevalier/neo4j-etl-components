package org.neo4j.mysql;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

import org.neo4j.mysql.config.ExportConfig;

import static java.util.Arrays.asList;

public class Export
{
    private final ExportConfig config;

    public Export( ExportConfig config )
    {
        this.config = config;
    }

    public Collection<Path> execute() throws Exception
    {
        String exportId = UUID.randomUUID().toString();

        Path headerFile = new HeaderFile( config ).create( exportId );
        Path exportFile = new ExportFile( config ).create( exportId );

        return asList( headerFile, exportFile );
    }
}
