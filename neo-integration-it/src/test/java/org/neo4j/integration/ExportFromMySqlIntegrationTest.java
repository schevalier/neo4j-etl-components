package org.neo4j.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.Result;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFixture;
import org.neo4j.integration.provisioning.scripts.MySql;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.junit.Assert.assertEquals;

public class ExportFromMySqlIntegrationTest
{
    private static final String NEWLINE = System.lineSeparator();

    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Rule
    public final ResourceRule<Server> mySqlServer = new ResourceRule<>(
            ServerFixture.server( tempDirectory.get(), MySql.startupScript() ) );

    @Test
    public void shouldCreateDatabaseOnDatabaseServer() throws Exception
    {
        String ipAddress = mySqlServer.get().ipAddress();

        executeSql( ipAddress, MySql.setupDatabaseScript().value() );

        String expectedResults = "id\tusername\taddressId\n" +
                "1\tuser-1\t1" + NEWLINE +
                "2\tuser-2\t1" + NEWLINE +
                "3\tuser-3\t1" + NEWLINE +
                "4\tuser-4\t2" + NEWLINE +
                "5\tuser-5\t2" + NEWLINE +
                "6\tuser-6\t2" + NEWLINE +
                "7\tuser-7\t3" + NEWLINE +
                "8\tuser-8\t3" + NEWLINE +
                "9\tuser-9\t3";

        assertEquals( expectedResults, executeSql( ipAddress, "select * from javabase.Person;" ).stdout() );
    }

    private Result executeSql( String host, String sql ) throws Exception
    {
        return Commands.builder( "mysql",
                "--user=" + MySql.Parameters.DBUser.value(),
                "--password=" + MySql.Parameters.DBPassword.value(),
                "-h", host )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .redirectStdInFrom( tempFile( sql ) )
                .build()
                .execute()
                .await();
    }

    private Path tempFile( String contents ) throws IOException
    {
        Path file = Files.createTempFile( tempDirectory.get(), "", "" );
        Files.write( file, contents.getBytes() );
        return file;
    }
}
