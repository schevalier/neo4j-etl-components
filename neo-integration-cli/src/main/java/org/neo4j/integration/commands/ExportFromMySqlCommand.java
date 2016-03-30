package org.neo4j.integration.commands;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.config.Manifest;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportService;

import static java.lang.String.format;

public class ExportFromMySqlCommand
{
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final Environment environment;
    private MySqlExportService databaseExportService;
    private String database;

    public ExportFromMySqlCommand( String host,
                                   int port,
                                   String user,
                                   String password,
                                   Environment environment,
                                   String database )
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.environment = environment;
        this.databaseExportService = new MySqlExportService();
        this.database = database;
    }

    public void execute() throws Exception
    {
        Path csvDirectory = environment.prepare();

        print( format( "CSV directory: %s", csvDirectory ) );

        Formatting formatting = Formatting.DEFAULT;

        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( host )
                .port( port )
                .database( database )
                .username( user )
                .password( password )
                .build();

        print( "Exporting from MySQL to CSV..." );


        ExportToCsvConfig.Builder builder = ExportToCsvConfig.builder()
                .destination( csvDirectory )
                .connectionConfig( connectionConfig )
                .formatting( formatting );

        SchemaExport schemaExport = buildSchemaExport( connectionConfig );
        builder.addTables( schemaExport.tables() );
        builder.addJoins( schemaExport.joins() );
        builder.addJoinTables( schemaExport.joinTables() );
        ExportToCsvConfig config = builder.build();

        Manifest manifest = new ExportToCsvCommand( config, databaseExportService ).execute();

        print( "Creating Neo4j store from CSV..." );

        doImport( formatting, manifest );

        print( "Done" );
        printResult( environment.destinationDirectory() );
    }

    private SchemaExport buildSchemaExport( ConnectionConfig connectionConfig ) throws Exception
    {
        DatabaseInspector databaseInspector = new DatabaseInspector( new DatabaseClient( connectionConfig ) );
        return databaseInspector.buildSchemaExport();
    }

    private void doImport( Formatting formatting, Manifest manifest ) throws Exception
    {
        ImportConfig.Builder builder = ImportConfig.builder()
                .importToolDirectory( environment.importToolDirectory() )
                .destination( environment.destinationDirectory() )
                .formatting( formatting )
                .idType( IdType.String );

        manifest.addNodesAndRelationshipsToBuilder( builder );

        new ImportFromCsvCommand( builder.build() ).execute();
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
