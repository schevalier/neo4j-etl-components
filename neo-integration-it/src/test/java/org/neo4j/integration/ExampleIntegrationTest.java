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

public class ExampleIntegrationTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Rule
    public final ResourceRule<Server> mySqlServer = new ResourceRule<>(
            ServerFixture.server( tempDirectory.get(), MySql.startupScript() ) );

    @Test
    public void shouldCreateDatabaseOnDatabaseServer() throws Exception
    {
        String ipAddress = mySqlServer.get().ipAddress();

        System.out.println( executeSql( ipAddress, MySql.setupDatabaseScript().value() ) );
        System.out.println( executeSql( ipAddress, "select * from javabase.Person;" ) );
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
