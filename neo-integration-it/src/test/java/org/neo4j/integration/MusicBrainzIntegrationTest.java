package org.neo4j.integration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
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

@Ignore
public class MusicBrainzIntegrationTest
{
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

    @BeforeClass
    public static void setUp() throws Exception
    {
        LogManager.getLogManager().readConfiguration(
                NeoIntegrationCli.class.getResourceAsStream( "/debug-logging.properties" ) );
        exportFromMySqlToNeo4j( "ngsdb" );
        neo4j.get().start();
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

//        String response = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (p:Person)-[r]->(c:Address) RETURN p, type" +
//                "(r), c" );
//        List<String> usernames = JsonPath.read( response, "$.results[*].data[*].row[0].username" );
//        List<String> relationships = JsonPath.read( response, "$.results[*].data[*].row[1]" );
//        List<String> postcodes = JsonPath.read( response, "$.results[*].data[*].row[2].postcode" );
//
//        assertThat( usernames.size(), is( 9 ) );
//
//        assertThat( usernames, hasItems(
//                "user-1", "user-2", "user-3", "user-4", "user-5", "user-6", "user-7", "user-8", "user-9" ) );
//        assertEquals( asList( "ADDRESS", "ADDRESS", "ADDRESS", "ADDRESS", "ADDRESS", "ADDRESS",
//                "ADDRESS", "ADDRESS", "ADDRESS" ), relationships );
//        assertThat( postcodes, hasItems( "AB12 1XY", "XY98 9BA", "ZZ1 0MN" ) );
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

        String[] args = {"mysql",
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
                "--debug"};
        System.out.println( ToStringBuilder.reflectionToString( args ) );
        NeoIntegrationCli.executeMainReturnSysOut(
                args );
    }
}
