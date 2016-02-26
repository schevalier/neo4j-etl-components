package org.neo4j.integration.neo4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.Result;

public class Neo4j implements AutoCloseable
{
    private final Path directory;

    public Neo4j( Path directory )
    {
        this.directory = directory;
    }

    public Path binDirectory()
    {
        return directory.resolve( "bin" );
    }

    public Path databasesDirectory()
    {
        return directory.resolve( "data/databases" );
    }

    public void disableAuth() throws IOException
    {
        Path confFile = directory.resolve( "conf/neo4j.conf" );
        Properties properties = new Properties();
        properties.load( Files.newInputStream( confFile ) );
        properties.setProperty( "dbms.security.auth_enabled", "false" );
        try ( BufferedWriter writer = Files.newBufferedWriter( confFile ) )
        {
            properties.store( writer, "" );
        }
    }

    public void start() throws Exception
    {
        Result.Evaluator resultEvaluator = r ->
                (r.exitValue() == 0) ||
                        (r.exitValue() == 1 && r.stdout().startsWith( "Service is already running" ));

        Commands.builder( "bin/neo4j", "start" )
                .workingDirectory( directory )
                .commandResultEvaluator( resultEvaluator )
                .timeout( 10, TimeUnit.SECONDS )
                .inheritEnvironment()
                .build()
                .execute()
                .await();
        Thread.sleep( 10000 );
    }

    public void stop() throws Exception
    {
        Commands.builder( "bin/neo4j", "stop" )
                .workingDirectory( directory )
                .failOnNonZeroExitValue()
                .timeout( 10, TimeUnit.SECONDS )
                .inheritEnvironment()
                .build()
                .execute()
                .await();
    }

    public String execute( String command ) throws Exception
    {
        Result result = Commands.builder( "bin/neo4j-shell", "-c", command )
                .workingDirectory( directory )
                .failOnNonZeroExitValue()
                .timeout( 10, TimeUnit.SECONDS )
                .inheritEnvironment()
                .build()
                .execute()
                .await();

        return result.stdout();
    }

    @Override
    public void close() throws Exception
    {
        stop();
    }

    public String executeHttp( String uri, String request ) throws JsonProcessingException, InterruptedException
    {
        Client client = Client.create();

        ClientResponse post = client.resource( uri )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .post( ClientResponse.class, request );

        return post.getEntity( String.class );
    }
}
