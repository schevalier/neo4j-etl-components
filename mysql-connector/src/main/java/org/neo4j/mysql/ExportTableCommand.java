package org.neo4j.mysql;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

import org.neo4j.ingest.HeaderFile;
import org.neo4j.mysql.config.ExportProperties;
import org.neo4j.mysql.config.Table;

import static java.util.Arrays.asList;

class ExportTableCommand
{
    private final ExportProperties properties;
    private final Table table;

    public ExportTableCommand( ExportProperties properties, Table table )
    {
        this.properties = properties;
        this.table = table;
    }

    public Collection<Path> execute() throws Exception
    {
        String exportId = UUID.randomUUID().toString();

        Path headerFile = new HeaderFile( properties.destination(), properties.formatting() )
                .create( table.fieldMappings(), exportId );
        Path exportFile = new ExportDatabaseContentsCommand( properties ).execute( table, exportId );

        return asList( headerFile, exportFile );
    }
}
