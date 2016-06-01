package org.neo4j.integration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import static org.neo4j.integration.neo4j.Neo4j.NEO4J_VERSION;
import static org.neo4j.integration.neo4j.Neo4j.NEO_TX_URI;

public class MusicBrainzPerformanceTest
{
    @ClassRule
    public static final ResourceRule<Path> tempDirectory =
            new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @ClassRule
    public static final ResourceRule<Server> mySqlServer = new ResourceRule<>(
            ServerFixture.server(
                    "mysql-integration-test",
                    DatabaseType.MySQL.defaultPort(),
                    MySqlScripts.performanceStartupScript(),
                    tempDirectory.get() ) );

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
//                    "ngsdb.sql",
//                    MySqlClient.Parameters.DBUser.value(),
//                    MySqlClient.Parameters.DBPassword.value(),
//                    mySqlServer.get().ipAddress() );
            exportFromMySqlToNeo4j( "ngsdb" );
            neo4j.get().start();
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
        // then
        assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );
        String response = neo4j.get().executeHttp( NEO_TX_URI,
                "MATCH (label:Label{name : \"EMI Group\"})--(labelType:LabelType) RETURN label, labelType" );
        List<String> label = JsonPath.read( response, "$.results[*].data[*].row[0].name" );
        List<String> labelType = JsonPath.read( response, "$.results[*].data[*].row[1].name" );
        assertThat( label.get( 0 ), is( "EMI Group" ) );
        assertThat( labelType.get( 0 ), is( "Holding" ) );
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

        NeoIntegrationCli.executeMainReturnSysOut( new String[]{"mysql",
                "export",
                "--host", mySqlServer.get().ipAddress(),
                "--user", MySqlClient.Parameters.DBUser.value(),
                "--password", MySqlClient.Parameters.DBPassword.value(),
                "--database", database,
                "--import-tool", neo4j.get().binDirectory().toString(),
                "--options-file", importToolOptions.toString(),
                "--csv-directory", tempDirectory.get().toString(),
                "--destination", neo4j.get().databasesDirectory().resolve( Neo4j.DEFAULT_DATABASE ).toString(),
                "--force",
                "--debug"} );
    }

}
