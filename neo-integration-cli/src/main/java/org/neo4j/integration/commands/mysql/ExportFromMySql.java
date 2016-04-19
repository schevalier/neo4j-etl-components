package org.neo4j.integration.commands.mysql;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.neo4j.integration.environment.Environment;
import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.config.Manifest;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;

public class ExportFromMySql implements Callable<Void>
{
    public interface Events
    {
        Events EMPTY = new Events()
        {
            @Override
            public void onExportingToCsv( Path csvDirectory )
            {
                // Do nothing
            }

            @Override
            public void onCreatingNeo4jStore()
            {
                // Do nothing
            }

            @Override
            public void onExportComplete( Path destinationDirectory )
            {
                // Do nothing
            }
        };

        void onExportingToCsv( Path csvDirectory );

        void onCreatingNeo4jStore();

        void onExportComplete( Path destinationDirectory );
    }

    private final Events events;
    private final CsvResources csvResources;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final Environment environment;

    public ExportFromMySql( CsvResources csvResources,
                            ConnectionConfig connectionConfig,
                            Formatting formatting,
                            Environment environment )
    {
        this( Events.EMPTY, csvResources, connectionConfig, formatting, environment );
    }

    public ExportFromMySql( Events events,
                            CsvResources csvResources,
                            ConnectionConfig connectionConfig,
                            Formatting formatting,
                            Environment environment )
    {
        this.events = events;
        this.csvResources = csvResources;
        this.connectionConfig = connectionConfig;
        this.formatting = formatting;
        this.environment = environment;
    }

    @Override
    public Void call() throws Exception
    {
        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( environment.csvDirectory() )
                .connectionConfig( connectionConfig )
                .formatting( formatting )
                .build();

        events.onExportingToCsv( environment.csvDirectory() );

        Manifest manifest = new ExportToCsvCommand( config, csvResources ).execute();

        events.onCreatingNeo4jStore();

        doImport( formatting, manifest );

        events.onExportComplete( environment.destinationDirectory() );

        return null;
    }

    private void doImport( Formatting formatting, Manifest manifest ) throws Exception
    {
        ImportConfig.Builder builder = ImportConfig.builder()
                .importToolDirectory( environment.importToolDirectory() )
                .importToolOptions( environment.importToolOptions() )
                .destination( environment.destinationDirectory() )
                .formatting( formatting )
                .idType( IdType.String );

        manifest.addNodesAndRelationshipsToBuilder( builder );

        new ImportFromCsvCommand( builder.build() ).execute();
    }
}
