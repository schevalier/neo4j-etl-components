package org.neo4j.mysql;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.ingest.config.GraphDataConfig;
import org.neo4j.ingest.config.GraphDataConfigSupplier;
import org.neo4j.ingest.config.NodeConfig;
import org.neo4j.ingest.config.RelationshipConfig;
import org.neo4j.mysql.config.Join;
import org.neo4j.mysql.config.RelationalDatabaseExportConfig;
import org.neo4j.mysql.config.Table;

public class ExportCommand
{
    private final RelationalDatabaseExportConfig exportConfig;

    public ExportCommand( RelationalDatabaseExportConfig exportConfig )
    {
        this.exportConfig = exportConfig;
    }

    public GraphDataConfig execute() throws Exception
    {
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
