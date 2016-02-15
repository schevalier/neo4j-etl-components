package org.neo4j.integration.mysql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.Result;

public class MySqlClient
{
    public MySqlClient( Path tempDirectory, String host )
    {
        this.tempDirectory = tempDirectory;
        this.host = host;
    }

    public enum Parameters
    {
        DBRootPassword(  "xsjhdcfhsd" ), DBUser( "neo" ), DBPassword( "neo" );

        private final String value;

        Parameters( String value )
        {
            this.value = value;
        }

        public String value()
        {
            return value;
        }
    }

    private final Path tempDirectory;
    private final String host;

    public Result execute( String sql ) throws Exception
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
        Path file = Files.createTempFile( tempDirectory, "", "" );
        Files.write( file, contents.getBytes() );
        return file;
    }
}
