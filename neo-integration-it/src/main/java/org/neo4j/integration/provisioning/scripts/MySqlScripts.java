package org.neo4j.integration.provisioning.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import com.amazonaws.util.IOUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.stringtemplate.v4.ST;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.provisioning.Script;

public class MySqlScripts
{

    public static final String AWS_SQL_FILE_BUCKET = "https://s3-eu-west-1.amazonaws.com/integration.neo4j.com/";

    public static Script startupScript()
    {
        return createScript( "/scripts/mysql-startup.sh" );
    }

    public static Script setupDatabaseScript()
    {
        return createScript( "/scripts/setup-db.sql" );
    }

    public static Script northwindScript()
    {
        return createScript( "/scripts/northwind.sql" );
    }

    private static Script createScript( String path )
    {
        return new Script()
        {
            @Override
            public String value() throws IOException
            {
                String script = IOUtils.toString( getClass().getResourceAsStream( path ) );

                ST template = new ST( script );

                for ( MySqlClient.Parameters parameter : MySqlClient.Parameters.values() )
                {
                    template.add( parameter.name(), parameter.value() );
                }

                return template.render();
            }
        };
    }

    public static void executeImportOfDatabase( Path tempDirectoryPath,
                                                final String databaseSqlFileName,
                                                String username,
                                                String password,
                                                String hostname ) throws Exception
    {
        Client httpClient = Client.create();

        ClientResponse response = null;

        try
        {
            response = httpClient.resource( AWS_SQL_FILE_BUCKET + databaseSqlFileName ).get( ClientResponse.class );
            Path fileOnDisk = tempDirectoryPath.resolve( databaseSqlFileName );

            try ( InputStream entityInputStream = response.getEntityInputStream() )
            {
                Files.copy( entityInputStream, fileOnDisk );

                Commands commands = Commands.builder(
                        new String[]{"mysql", "-u", username, "-p" + password, "-h", hostname} )
                        .inheritWorkingDirectory()
                        .failOnNonZeroExitValue()
                        .noTimeout()
                        .inheritEnvironment()
                        .redirectStdInFrom( fileOnDisk )
                        .build();
                commands.execute().await( 20, TimeUnit.MINUTES );
            }
        }
        catch ( Exception e )
        {
            if ( response != null )
            {
                response.close();
            }
            throw e;
        }
    }

}
