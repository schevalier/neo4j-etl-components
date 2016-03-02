package org.neo4j.integration.commands;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
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
    private MySqlExportService databaseExportService;

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
        this.databaseExportService = new MySqlExportService();
    }

    public void execute() throws Exception
    {
        Path csvDirectory = environment.prepare();

        print( format( "CSV directory: %s", csvDirectory ) );

        Formatting formatting = Formatting.DEFAULT;

        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( host )
                .port( port )
                .database( schemaDetails.database() )
                .username( user )
                .password( password )
                .build();

        print( "Exporting from MySQL to CSV..." );

        GraphConfig graphConfig = exportAndCreateGraphConfig( csvDirectory, formatting, connectionConfig );

        print( "Creating Neo4j store from CSV..." );

        doImport( formatting, graphConfig );

        print( "Done" );
        printResult( environment.destinationDirectory() );
    }

    private GraphConfig exportAndCreateGraphConfig( Path csvDirectory,
                                                    Formatting formatting,
                                                    ConnectionConfig connectionConfig ) throws Exception
    {
        SchemaExport schemaExport =
                schemaExportService.doExport( schemaDetails, () -> new DatabaseClient( connectionConfig ) );
        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( csvDirectory )
                .connectionConfig( connectionConfig )
                .formatting( formatting )
                .addTables( schemaExport.startTable() )
                .addTables( schemaExport.endTable() )
                .addJoins( schemaExport.joins() )
                .addJoinTables(schemaExport.joinTables())
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
