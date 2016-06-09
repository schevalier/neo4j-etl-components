package org.neo4j.integration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.neo4j.Neo4j;
import org.neo4j.integration.provisioning.Neo4jFixture;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFixture;
import org.neo4j.integration.provisioning.scripts.MySqlScripts;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.junit.Assert.assertFalse;

import static org.neo4j.integration.neo4j.Neo4j.NEO4J_VERSION;
import static org.neo4j.integration.neo4j.Neo4j.NEO_TX_URI;
import static org.neo4j.integration.provisioning.platforms.TestType.INTEGRATION;

public class BigPerformanceTest
{
    @ClassRule
    public static final ResourceRule<Path> tempDirectory =
            new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @ClassRule
    public static final ResourceRule<Server> mySqlServer = new ResourceRule<>(
            ServerFixture.server(
                    "mysql-integration-test",
                    DatabaseType.MySQL.defaultPort(),
                    MySqlScripts.bigPerformanceStartupScript(),
                    tempDirectory.get(),
                    INTEGRATION ) );

    @ClassRule
    public static final ResourceRule<Neo4j> neo4j = new ResourceRule<>(
            Neo4jFixture.neo4j( NEO4J_VERSION, tempDirectory.get() ) );

    @BeforeClass
    public static void setUp() throws Exception
    {
        try
        {
            LogManager.getLogManager().readConfiguration(
                    NeoIntegrationCli.class.getResourceAsStream( "/debug-logging.properties" ) );
//            ServerFixture.executeImportOfDatabase( tempDirectory.get(),
//                    "northwind.sql",
//                    MySqlClient.Parameters.DBUser.value(),
//                    MySqlClient.Parameters.DBPassword.value(),
//                    mySqlServer.get().ipAddress() );

        }
        catch ( IOException e )
        {
            System.err.println( "Error in loading configuration" );
            e.printStackTrace( System.err );
        }
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        neo4j.get().stop();
    }

    @Test
    public void shouldExportFromMySqlAndImportIntoGraph() throws Exception
    {
        exportFromMySqlToNeo4j( "northwind" );
        neo4j.get().start();

        // then
        assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

        String customersJson = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (c:Customer) RETURN c" );
        String customersWithOrdersJson = neo4j.get().executeHttp( NEO_TX_URI,
                "MATCH (c)--(o) " +
                        "WHERE (c:Customer)<-[:CUSTOMER]-(o:Order) RETURN DISTINCT c" );
        List<String> customers = JsonPath.read( customersJson, "$.results[*].data[*].row[0]" );
        List<String> customersWithOrders = JsonPath.read( customersWithOrdersJson, "$.results[*].data[*].row[0]" );
        MatcherAssert.assertThat( customers.size(), CoreMatchers.is( 93 ) );
        MatcherAssert.assertThat( customersWithOrders.size(), CoreMatchers.is( 89 ) );

    }

    private static void exportFromMySqlToNeo4j( String database ) throws IOException
    {
        Path importToolOptions = tempDirectory.get().resolve( "import-tool-options.json" );
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<Object, Object> options = new HashMap<>();
        options.put( "delimiter", "\t" );
        options.put( "quote", "`" );
        options.put( "multiline-fields", "true" );
        objectMapper.writeValue( importToolOptions.toFile(), options );

        NeoIntegrationCli.executeMainReturnSysOut(
                new String[]{"mysql",
                        "export",
                        "--host", mySqlServer.get().ipAddress(),
                        "--user", MySqlClient.Parameters.DBUser.value(),
                        "--password", MySqlClient.Parameters.DBPassword.value(),
                        "--database", database,
                        "--import-tool", neo4j.get().binDirectory().toString(),
                        "--options-file", importToolOptions.toString(),
                        "--csv-directory", tempDirectory.get().toString(),
                        "--destination", neo4j.get().databasesDirectory().resolve( Neo4j.DEFAULT_DATABASE ).toString(),
                        "--force"} );
    }
}
