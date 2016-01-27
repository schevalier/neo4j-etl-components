package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.neo4j.importcsv.HeaderFileWriter;
import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.metadata.DatabaseObject;

public class ExportToCsv
{
    private final ExportToCsvConfig config;
    private final DatabaseExportProvider databaseExportProvider;

    public ExportToCsv( ExportToCsvConfig config, DatabaseExportProvider databaseExportProvider )
    {
        this.config = config;
        this.databaseExportProvider = databaseExportProvider;
    }

    public GraphConfig execute() throws Exception
    {
        if ( Files.notExists( config.destination() ) )
        {
            Files.createDirectories( config.destination() );
        }

        Commands.commands( "chmod", "0777", config.destination().toString() ).execute().await();

        Collection<GraphDataConfig> graphDataConfig = new ArrayList<>();

        try ( SqlRunner sqlRunner = new SqlRunner( config.connectionConfig() ) )
        {
            HeaderFileWriter headerFileWriter = new HeaderFileWriter( config.destination(), config.formatting() );
            ExportFileWriter exportFileWriter = databaseExportProvider.createExportFileWriter( config, sqlRunner );

            for ( DatabaseObject databaseObject : config.databaseObjects() )
            {
                graphDataConfig.add(
                        databaseExportProvider.exportDatabaseObject(
                                databaseObject,
                                headerFileWriter,
                                exportFileWriter,
                                config
                        ) );
            }
        }

        return new GraphConfig( graphDataConfig );
    }
}
