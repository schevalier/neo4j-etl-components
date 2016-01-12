package org.neo4j.integration.mysql.exportcsv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfigSupplier;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.mysql.exportcsv.config.Join;
import org.neo4j.integration.mysql.exportcsv.config.RelationalDatabaseExportConfig;
import org.neo4j.integration.mysql.exportcsv.config.Table;

public class ExportCommand
{
    private final RelationalDatabaseExportConfig exportConfig;

    public ExportCommand( RelationalDatabaseExportConfig exportConfig )
    {
        this.exportConfig = exportConfig;
    }

    public GraphDataConfig execute() throws Exception
    {
        if ( Files.notExists( exportConfig.destination() ))
        {
            Files.createDirectories( exportConfig.destination() );
        }

        Commands.commands( "chmod", "0777", exportConfig.destination().toString() ).execute().await();

        Collection<GraphDataConfigSupplier> graphDataConfigSuppliers = new ArrayList<>();

        for ( Table table : exportConfig.tables() )
        {
            ExportTableCommand exportTableCommand = new ExportTableCommand( exportConfig, table );
            Collection<Path> files = exportTableCommand.execute();

            graphDataConfigSuppliers.add( NodeConfig.builder()
                    .addInputFiles( files )
                    .addLabel( table.name().simpleName() )
                    .build() );
        }

        for ( Join join : exportConfig.joins() )
        {
            ExportJoinCommand exportJoinCommand = new ExportJoinCommand( exportConfig, join );
            Collection<Path> files = exportJoinCommand.execute();

            graphDataConfigSuppliers.add( RelationshipConfig.builder()
                    .addInputFiles( files )
                    .build() );
        }

        return new GraphDataConfig( graphDataConfigSuppliers );
    }
}
