package org.neo4j.integration.mysql.exportcsv;

import java.nio.file.Path;

import org.neo4j.integration.mysql.SqlRunner;
import org.neo4j.integration.mysql.exportcsv.config.ExportProperties;
import org.neo4j.integration.mysql.exportcsv.config.SqlSupplier;

import static java.lang.String.format;

import static org.neo4j.integration.util.StringListBuilder.stringList;

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

        SqlRunner sqlRunner = new SqlRunner(
                properties.connectionConfig(),
                sqlSupplier.sql( exportFile, properties.formatting().delimiter() ) );
        sqlRunner.execute().await();

        return exportFile;
    }
}
