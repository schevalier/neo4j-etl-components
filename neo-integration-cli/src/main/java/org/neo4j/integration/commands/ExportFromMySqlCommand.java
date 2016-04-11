package org.neo4j.integration.commands;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.config.Manifest;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportSqlSupplier;

import static java.lang.String.format;

public class ExportFromMySqlCommand
{
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final Environment environment;
    private final DatabaseExportSqlSupplier sqlSupplier;
    private final String database;
    private final Formatting formatting;

    public ExportFromMySqlCommand( String host,
                                   int port,
                                   String user,
                                   String password,
                                   String database,
                                   Delimiter delimiter,
                                   Environment environment )
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.environment = environment;
        this.sqlSupplier = new MySqlExportSqlSupplier();
        this.database = database;
        this.formatting = Formatting.builder().delimiter( delimiter ).build();
    }

    public void execute() throws Exception
    {
        Path csvDirectory = environment.prepare();

        print( format( "CSV directory: %s", csvDirectory ) );
        print( "Creating MySQL to CSV mappings..." );

        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( host )
                .port( port )
                .database( database )
                .username( user )
                .password( password )
                .build();

        SchemaExport schemaExport = buildSchemaExport( connectionConfig );
        CsvResources csvResources = schemaExport.createCsvResources( formatting, sqlSupplier );

        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( csvDirectory )
                .connectionConfig( connectionConfig )
                .formatting( formatting )
                .build();

        print( "Exporting from MySQL to CSV..." );

        Manifest manifest = new ExportToCsvCommand( config, csvResources ).execute();

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
