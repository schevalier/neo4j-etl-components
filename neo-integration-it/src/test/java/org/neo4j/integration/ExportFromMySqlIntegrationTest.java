package org.neo4j.integration;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.neo4j.Neo4j;
import org.neo4j.integration.neo4j.Neo4jVersion;
import org.neo4j.integration.provisioning.Neo4jFixture;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFixture;
import org.neo4j.integration.provisioning.scripts.MySqlScripts;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ExportFromMySqlIntegrationTest
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
        exportFromMySqlToNeo4j();
    }

    @Test
    public void shouldExportFromMySqlAndImportIntoGraph() throws Exception
    {
        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String response = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (p:Person)-[r]->(c:Address) RETURN p, type" +
                    "(r), c" );
            List<String> usernames = JsonPath.read( response, "$.results[*].data[*].row[0].username" );
            List<String> relationships = JsonPath.read( response, "$.results[*].data[*].row[1]" );
            List<String> postcodes = JsonPath.read( response, "$.results[*].data[*].row[2].postcode" );

            assertThat( usernames.size(), is( 9 ) );

            assertThat( usernames, hasItems(
                    "user-1", "user-2", "user-3", "user-4", "user-5", "user-6", "user-7", "user-8", "user-9" ) );
            assertEquals( asList( "ADDRESS", "ADDRESS", "ADDRESS", "ADDRESS", "ADDRESS", "ADDRESS",
                    "ADDRESS", "ADDRESS", "ADDRESS" ), relationships );
            assertThat( postcodes, hasItems( "AB12 1XY", "XY98 9BA", "ZZ1 0MN" ) );
        }
        finally
        {
            neo4j.get().stop();
        }
    }

    @Test
    public void shouldExportTableWithCompositeJoinColumns() throws Exception
    {
        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String response = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (p:Book)-[r]->(c:Author) RETURN p, type(r)," +
                    " c" );
            List<String> books = JsonPath.read( response, "$.results[*].data[*].row[0].name" );
            List<String> relationships = JsonPath.read( response, "$.results[*].data[*].row[1]" );
            List<String> lastNames = JsonPath.read( response, "$.results[*].data[*].row[2].lastName" );

            assertThat( books.size(), is( 2 ) );

            assertThat( books, hasItems( "Database System Concepts" ) );
            assertEquals( asList( "AUTHOR", "AUTHOR" ), relationships );
            assertEquals( asList( "Silberschatz", "Tanenbaum" ), lastNames );
        }
        finally
        {
            neo4j.get().stop();
        }
    }

    @Test
    public void shouldExportFromMySqlAndImportIntoGraphForNumericAndStringTables() throws Exception
    {
        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String response = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (p:StringTable)-[r]->(c:NumericTable) " +
                    "RETURN p, c" );
            System.out.println( response );

            List<Map<String, String>> stringFields = JsonPath.read( response, "$.results[*].data[0].row[0]" );
            List<Map<String, Object>> numericFields = JsonPath.read( response, "$.results[*].data[0].row[1]" );

            assertThat( stringFields.get( 0 ).values(), hasItems(
                    "val-1", "mediumtext_field", "longblob_field", "blob_field", "tinytext_field", "mediumblob_field",
                    "char-field", "text_field", "varchar-field", "tinyblob_field", "longtext_field" ) );
            assertThat( numericFields.get( 0 ).values(), hasItems( 123, 123, 123.2, 123, 18.0, 1.232343445E7, 1 ) );
        }
        finally
        {
            neo4j.get().stop();
        }
    }

    @Test
    public void shouldExportFromMySqlAndImportIntoGraphForNumericAndDateTables() throws Exception
    {
        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String response = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (p:DateTable)-[r]->(c:NumericTable) " +
                    "RETURN p, c" );

            List<Map<String, Object>> dateFields = JsonPath.read( response, "$.results[*].data[0].row[0]" );
            List<Map<String, Object>> numericFields = JsonPath.read( response, "$.results[*].data[0].row[1]" );
            assertThat( dateFields.get( 0 ).values(), hasItems(
                    "22:34:35",
                    "1987-01-01",
                    "1989-01-23 00:00:00.0",
                    "2038-01-19 03:14:07.0",
                    "1988-01-23" ) );
            assertThat( numericFields.get( 0 ).values(), hasItems( 123, 123, 123.2, 123, 18.0, 1.232343445E7, 1 ) );
        }
        finally
        {
            neo4j.get().stop();
        }
    }

    @Test
    public void shouldExportFromMySqlAndImportIntoGraphForThreeTableJoinWithProperties() throws Exception
    {
        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String response = neo4j.get()
                    .executeHttp( NEO_TX_URI, "MATCH (p:Student)<-[r]-(c:Course) RETURN p, c, r.credits" );

            List<String> students = JsonPath.read( response, "$.results[*].data[*].row[0].username" );
            List<String> courses = JsonPath.read( response, "$.results[*].data[*].row[1].name" );
            List<Integer> credits = JsonPath.read( response, "$.results[*].data[*].row[2]" );

            assertThat( students.size(), is( 4 ) );
            assertThat( students, hasItems( "jim", "mark" ) );


            assertThat( courses.size(), is( 4 ) );
            assertThat( courses, hasItems( "Science", "Maths", "English" ) );

            assertThat( credits, hasItems( 1, 2, 3, 4 ) );

            String coursesResponse = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (c:Course) RETURN c.name" );
            String studentsResponse = neo4j.get().executeHttp( NEO_TX_URI, "MATCH (s:Student) RETURN s.username" );

            List<String> allCourses = JsonPath.read( coursesResponse, "$.results[*].data[*].row[0]" );
            List<String> allStudents = JsonPath.read( studentsResponse, "$.results[*].data[*].row[0]" );

            assertThat( allStudents, hasItems( "jim", "mark", "eve" ) );
            assertThat( allCourses, hasItems( "Science", "Maths", "English", "Theology" ) );
        }
        finally
        {
            neo4j.get().stop();
        }
    }

    @Test
    @Ignore
    public void shouldExportFromMySqlAndImportIntoGraphForCompositeThreeTableJoinWithProperties() throws Exception
    {
        // when
//        exportFromMySqlToNeo4j( "Author", "Publisher", "Author_Publisher" );

        // then
        try
        {
            neo4j.get().start();

            assertFalse( neo4j.get().containsImportErrorLog( Neo4j.DEFAULT_DATABASE ) );

            String response = neo4j.get()
                    .executeHttp( NEO_TX_URI, "MATCH (a:Author)-[r]->(p:Publisher) RETURN a, p" );

            List<String> authors = JsonPath.read( response, "$.results[*].data[*].row[0].last_name" );
            List<String> publishers = JsonPath.read( response, "$.results[*].data[*].row[1].name" );

            assertThat( authors.size(), is( 2 ) );
            assertThat( authors, hasItems( "Tanenbaum", "Silberschatz" ) );

            assertThat( publishers.size(), is( 2 ) );
            assertThat( publishers, hasItems( "Pearson", "O'Reilly" ) );
        }
        finally
        {
            neo4j.get().stop();
        }
    }

    private static void exportFromMySqlToNeo4j()
    {
        NeoIntegrationCli.executeMainReturnSysOut(
                new String[]{"mysql-export",
                        "--host", mySqlServer.get().ipAddress(),
                        "--user", MySqlClient.Parameters.DBUser.value(),
                        "--password", MySqlClient.Parameters.DBPassword.value(),
                        "--database", MySqlClient.Parameters.Database.value(),
                        "--import-tool", neo4j.get().binDirectory().toString(),
                        "--csv-directory", tempDirectory.get().toString(),
                        "--destination", neo4j.get().databasesDirectory().resolve( Neo4j.DEFAULT_DATABASE ).toString(),
                        "--force"} );
    }

    private static void populateMySqlDatabase() throws Exception
    {
        MySqlClient client = new MySqlClient( mySqlServer.get().ipAddress() );
        client.execute( MySqlScripts.setupDatabaseScript().value() );
    }
}
