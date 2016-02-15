package org.neo4j.integration;

import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.neo4j.Neo4j;
import org.neo4j.integration.neo4j.Neo4jVersion;
import org.neo4j.integration.provisioning.Neo4jFixture;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFixture;
import org.neo4j.integration.provisioning.scripts.MySqlScripts;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.Strings;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.junit.Assert.assertEquals;

public class ExportFromMySqlIntegrationTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Rule
    public final ResourceRule<Server> mySqlServer = new ResourceRule<>(
            ServerFixture.server( tempDirectory.get(), MySqlScripts.startupScript() ) );

    @Rule
    public final ResourceRule<Neo4j> neo4j = new ResourceRule<>(
            Neo4jFixture.neo4j( tempDirectory.get(), Neo4jVersion.v2_3_2 ) );

    @Test
    public void shouldExportFromMySqlAndImportIntoNeo4j() throws Exception
    {
        String ipAddress = mySqlServer.get().ipAddress();
        MySqlClient client = new MySqlClient( tempDirectory.get(), ipAddress );

        client.execute( MySqlScripts.setupDatabaseScript().value() );

        NeoIntegrationCli.main( new String[]{"mysql-export",
                "--host", ipAddress,
                "--user", MySqlClient.Parameters.DBUser.value(),
                "--password", MySqlClient.Parameters.DBPassword.value(),
                "--database", "javabase",
                "--import-tool", neo4j.get().binDirectory().toString(),
                "--destination", tempDirectory.get().toString(),
                "--parent", "Person",
                "--child", "Address"} );

        System.out.println( "Finished" );
    }

    @Test
    public void shouldCreateDatabaseOnDatabaseServer() throws Exception
    {
        MySqlClient client = new MySqlClient( tempDirectory.get(), mySqlServer.get().ipAddress() );

        client.execute( MySqlScripts.setupDatabaseScript().value() );

        String expectedResults = Strings.lineSeparated( "id\tusername\taddressId",
                "1\tuser-1\t1",
                "2\tuser-2\t1",
                "3\tuser-3\t1",
                "4\tuser-4\t2",
                "5\tuser-5\t2",
                "6\tuser-6\t2",
                "7\tuser-7\t3",
                "8\tuser-8\t3",
                "9\tuser-9\t3" );

        assertEquals( expectedResults, client.execute( "select * from javabase.Person;" ).stdout() );
    }


}
