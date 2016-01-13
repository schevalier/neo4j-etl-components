package org.neo4j.integration.mysql.exportcsv;

import java.nio.file.Path;

import org.neo4j.integration.mysql.SqlRunner;
import org.neo4j.integration.mysql.exportcsv.config.ExportProperties;
import org.neo4j.integration.mysql.exportcsv.config.SqlSupplier;

import static java.lang.String.format;

class ExportDatabaseContentsCommand
{
    private final ExportProperties properties;

    public ExportDatabaseContentsCommand( ExportProperties properties )
    {
        this.properties = properties;
    }

    public Path execute( SqlSupplier sqlSupplier, String exportId ) throws Exception
    {
        Path exportFile = properties.destination().resolve( format( "%s.csv", exportId ) );

        try ( SqlRunner sqlRunner = new SqlRunner( properties.connectionConfig() ) )
        {
            sqlRunner.execute( sqlSupplier.sql( exportFile, properties.formatting().delimiter() ) ).await();
        }

        return exportFile;
    }
}
