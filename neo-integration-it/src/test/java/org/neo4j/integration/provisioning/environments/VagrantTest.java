package org.neo4j.integration.provisioning.environments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.Result;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.scripts.MySqlScripts;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

public class VagrantTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Test
    @Ignore
    public void shouldStartVagrantBox() throws Exception
    {
        Path directory = Paths.get( "/Users/iansrobinson/Desktop/neo-mysql" );
        Server server = new Vagrant( directory ).createServer( MySqlScripts.startupScript() );
        executeSql( server.ipAddress(), MySqlScripts.setupDatabaseScript().value() );

        System.out.println( server.ipAddress() );
    }

    @Test
    @Ignore
    public void shouldDestroyVagrantBox() throws Exception
    {
        new Vagrant( Paths.get( "/Users/iansrobinson/Desktop/neo-mysql" ) ).destroy();
    }

    private Result executeSql( String host, String sql ) throws Exception
    {
        return Commands.builder( "mysql",
                "--user=" + MySqlClient.Parameters.DBUser.value(),
                "--password=" + MySqlClient.Parameters.DBPassword.value(),
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


