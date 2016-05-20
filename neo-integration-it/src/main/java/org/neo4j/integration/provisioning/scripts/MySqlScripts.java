package org.neo4j.integration.provisioning.scripts;

import java.io.IOException;

import com.amazonaws.util.IOUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.stringtemplate.v4.ST;

import org.neo4j.integration.mysql.MySqlClient;
import org.neo4j.integration.provisioning.Script;

public class MySqlScripts
{

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

    public static Script performanceScript()
    {
        return new Script()
        {
            @Override
            public String value() throws IOException
            {

                Client client = Client.create();

                ClientResponse response = null;

                try
                {
                    response = client
                            .resource( "https://s3-eu-west-1.amazonaws.com/integration.neo4j.com/northwind.sql" )
                            .get( ClientResponse.class );

                    return response.getEntity( String.class );
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
        };

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
}
