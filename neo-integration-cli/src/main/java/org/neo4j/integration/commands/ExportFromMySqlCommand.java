package org.neo4j.integration.commands;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportService;

import static java.lang.String.format;

public class ExportFromMySqlCommand
{
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final Environment environment;
    private final SchemaExportService schemaExportService;
    private final SchemaDetails schemaDetails;

    public ExportFromMySqlCommand( String host,
                                   int port,
                                   String user,
                                   String password,
                                   Environment environment,
                                   SchemaDetails schemaDetails )
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.schemaDetails = schemaDetails;
        this.environment = environment;
        this.schemaExportService = new SchemaExportService();
    }

    public void execute() throws Exception
    {
        Path csvDirectory = environment.prepare();

        print( format( "CSV directory: %s", csvDirectory ) );

        Formatting formatting = Formatting.DEFAULT;

        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( host )
                .port( port )
                .database( schemaDetails.getDatabase() )
                .username( user )
                .password( password )
                .build();

        print( "Exporting from MySQL to CSV..." );

        GraphConfig graphConfig = exportAndCreateGraphConfig( csvDirectory, formatting, connectionConfig, new MySqlExportService() );

        print( "Creating Neo4j store from CSV..." );

        doImport( formatting, graphConfig );

        print( "Done" );
        printResult( environment.destinationDirectory() );
    }

    private GraphConfig exportAndCreateGraphConfig( Path csvDirectory,
                                                    Formatting formatting,
                                                    ConnectionConfig connectionConfig,
                                                    MySqlExportService databaseExportService ) throws Exception
    {
        SchemaExport schemaExport = schemaExportService.doExport( connectionConfig, schemaDetails );
        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( csvDirectory )
                .connectionConfig( connectionConfig )
                .formatting( formatting )
                .addTables( schemaExport.getStartTable() )
                .addTables( schemaExport.getEndTable() )
                .addJoins( schemaExport.getJoins() )
                .build();

        ExportToCsvResults exportResults = new ExportToCsvCommand( config, databaseExportService ).execute();
        return exportResults.createGraphConfig();
    }

    private void doImport( Formatting formatting, GraphConfig graphConfig ) throws Exception
    {
        ImportConfig importConfig = ImportConfig.builder()
                .importToolDirectory( environment.importToolDirectory() )
                .destination( environment.destinationDirectory() )
                .formatting( formatting )
                .idType( IdType.Integer )
                .graphDataConfig( graphConfig )
                .build();

        new ImportFromCsvCommand( importConfig ).execute();
    }

    private void print( Object message )
    {
        System.err.println( message );
    }

    private void printResult( Object message )
    {
        System.out.println( message );
    }

}
