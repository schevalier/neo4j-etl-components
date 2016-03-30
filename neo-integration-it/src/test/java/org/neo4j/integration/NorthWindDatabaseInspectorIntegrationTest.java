package org.neo4j.integration;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.LogManager;

import com.jayway.jsonpath.JsonPath;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.commands.DatabaseInspector;
import org.neo4j.integration.commands.SchemaExport;
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
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportService;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

public class NorthWindDatabaseInspectorIntegrationTest
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
                    tempDirectory.get(),
                    "local" ) );

    @ClassRule
    public static final ResourceRule<Neo4j> neo4j = new ResourceRule<>(
            Neo4jFixture.neo4j( NEO4J_VERSION, tempDirectory.get() ) );
    public static final URI NEO_TX_URI = URI.create( "http://localhost:7474/db/data/transaction/commit" );

    @BeforeClass
    public static void setUp() throws Exception
    {
        populateMySqlDatabase();
        try
        {
            LogManager.getLogManager().readConfiguration(
                    NeoIntegrationCli.class.getResourceAsStream( "/logging.properties" ) );
        }
        catch ( IOException e )
        {
            System.err.println( "Error in loading configuration" );
            e.printStackTrace( System.err );
        }
    }

    @Test
    @Ignore
    public void shouldExportFromMySqlAndImportIntoGraph() throws Exception
    {
        // when
        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL ).host( "localhost" )
                .port( DatabaseType.MySQL.defaultPort() )
                .database( "northwind" ).username( "neo" ).password( "neo" ).build();

        DatabaseClient databaseClient = new DatabaseClient( connectionConfig );

        ExportToCsvConfig.Builder builder = ExportToCsvConfig.builder()
                .destination( tempDirectory.get() )
                .connectionConfig( connectionConfig )
                .formatting( Formatting.builder().delimiter( Delimiter.TAB ).build() );

        DatabaseInspector databaseInspector = new DatabaseInspector( databaseClient );

        SchemaExport schemaExport = databaseInspector.buildSchemaExport();
        builder.addTables( schemaExport.tables() );
        builder.addJoins( schemaExport.joins() );
        builder.addJoinTables( schemaExport.joinTables() );

        ExportToCsvConfig config = builder.build();

        Manifest manifest = new ExportToCsvCommand( config, new MySqlExportService() ).execute();

        doImport( Formatting.builder().delimiter( Delimiter.TAB ).build(), manifest );

        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String customersJson = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (c) WHERE (c:Customer) RETURN c" );
            String customersWithOrdersJson = neo4j.get().executeHttp( NEO_TX_URI,
                    "MATCH (c)--(o) " +
                            "WHERE (c:Customer)<-[:CUSTOMER]-(o:Order) RETURN DISTINCT c" );
            List<String> customers = JsonPath.read( customersJson, "$.results[*].data[*].row[0]" );
            List<String> customersWithOrders = JsonPath.read( customersWithOrdersJson, "$.results[*].data[*].row[0]" );
            assertThat( customers.size(), is( 29 ) );
            assertThat( customersWithOrders.size(), is( 15 ) );

            String newOrdersJson = neo4j.get().executeHttp( NEO_TX_URI,
                    "MATCH (o)--(os) " +
                            "WHERE (os:OrderStatus{statusName:'New'})--(o:Order) RETURN o" );
            List<String> newOrders = JsonPath.read( newOrdersJson, "$.results[*].data[*].row[0]" );

            assertThat( newOrders.size(), is( 16 ) );

            String employeeWithPrivilegesResponse = neo4j.get().executeHttp( NEO_TX_URI,
                    "MATCH (e:Employee)-[:EMPLOYEE_PRIVILEGE]->(p) RETURN e,p;" );
            List<String> city = JsonPath.read( employeeWithPrivilegesResponse, "$.results[*].data[*].row[0].city" );
            List<String> privilegeName = JsonPath.read( employeeWithPrivilegesResponse, "$.results[*].data[*].row[1]" +
                    ".privilegeName" );

            assertThat( city.get( 0 ), is( "Bellevue" ) );
            assertThat( privilegeName.get( 0 ), is( "Purchase Approvals" ) );

            String productsOnHold = neo4j.get().executeHttp( NEO_TX_URI,
                    "MATCH (p:Product)<--(n:InventoryTransaction)-->" +
                            "(it:InventoryTransactionType{typeName:'On Hold'}) " +
                            "RETURN DISTINCT p" );
            List<String> productName = JsonPath.read( productsOnHold, "$.results[*].data[*].row[0].productName" );
            assertThat( productName.get( 0 ), is( "Northwind Traders Gnocchi" ) );
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
                .destination( neo4j.get().databasesDirectory().resolve( Neo4j.DEFAULT_DATABASE ) )
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
