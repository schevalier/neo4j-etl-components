package org.neo4j.integration;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.neo4j.Neo4j;
import org.neo4j.integration.neo4j.Neo4jVersion;
import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.config.Manifest;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.provisioning.Neo4jFixture;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFixture;
import org.neo4j.integration.provisioning.scripts.MySqlScripts;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.DatabaseExport;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportService;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinTableMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static java.util.Arrays.asList;

public class MySqlDatabaseExportIntegrationTest
{
    private static final Neo4jVersion NEO4J_VERSION = Neo4jVersion.v3_0_0_M04;

    @ClassRule
    public static final ResourceRule<Path> tempDirectory =
            new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @ClassRule
    public static final ResourceRule<Server> mySqlServer = new ResourceRule<>(
            ServerFixture.server(
                    "mysql-integration-test",
                    DatabaseType.MySQL.defaultPort(),
                    MySqlScripts.startupScript(),
                    tempDirectory.get() ) );

    @ClassRule
    public static final ResourceRule<Neo4j> neo4j = new ResourceRule<>(
            Neo4jFixture.neo4j( NEO4J_VERSION, tempDirectory.get() ) );
    public static final URI NEO_TX_URI = URI.create( "http://localhost:7474/db/data/transaction/commit" );

    @BeforeClass
    public static void setUp() throws Exception
    {
        populateMySqlDatabase();
    }

    @Test
    @Ignore
    public void shouldExportFromMySqlAndImportIntoGraph() throws Exception
    {
        // when
        List<String> tableNames = asList(
                "customers",
                "employee_privileges",
                "employees",
                "inventory_transaction_types",
                "inventory_transactions",
                "invoices",
                "order_details",
                "order_details_status",
                "orders",
                "orders_status",
                "orders_tax_status",
                "privileges",
                "products",
                "purchase_order_details",
                "purchase_order_status",
                "purchase_orders",
//                "sales_reports",
                "shippers",
//                "strings",
                "suppliers" );
        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL ).host( "localhost" )
                .port( DatabaseType.MySQL.defaultPort() )
                .database( "northwind" ).username( "root" ).password( "password" ).build();
        DatabaseClient databaseClient = new DatabaseClient( connectionConfig );
        TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );
        JoinMetadataProducer joinMetadataProducer = new JoinMetadataProducer( databaseClient );
        JoinTableMetadataProducer joinTableMetadataProducer = new JoinTableMetadataProducer( databaseClient );
        DatabaseExport databaseExport = new DatabaseExport( tableMetadataProducer, joinMetadataProducer,
                joinTableMetadataProducer, databaseClient );
        ExportToCsvConfig.Builder builder = ExportToCsvConfig.builder()
                .destination( tempDirectory.get() )
                .connectionConfig( connectionConfig )
                .formatting( Formatting.builder().delimiter( Delimiter.TAB ).build() );
        for ( String tableName : tableNames )
        {
            databaseExport.updateConfig( builder, new TableName( "northwind", tableName ) );

        }
        ExportToCsvConfig config = builder.build();

        Manifest manifest = new ExportToCsvCommand( config, new MySqlExportService() ).execute();

        doImport( Formatting.builder().delimiter( Delimiter.TAB ).build(), manifest );

        // then
        try
        {
            neo4j.get().start();

            String response = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (n) RETURN n" );
            System.out.println( response );

        }
        finally
        {
            neo4j.get().stop();
        }
    }

    private void doImport( Formatting formatting, Manifest manifest ) throws Exception
    {
        ImportConfig.Builder builder = ImportConfig.builder()
                .importToolDirectory( neo4j.get().binDirectory() )
                .destination( neo4j.get().databasesDirectory().resolve( "graph.db" ) )
                .formatting( formatting )
                .idType( IdType.String );

        manifest.addNodesAndRelationshipsToBuilder( builder );

        new ImportFromCsvCommand( builder.build() ).execute();
    }

    private static void populateMySqlDatabase() throws Exception
    {
        MySqlClient client = new MySqlClient( mySqlServer.get().ipAddress() );
        client.execute( MySqlScripts.setupDatabaseScript().value() );
    }
}
