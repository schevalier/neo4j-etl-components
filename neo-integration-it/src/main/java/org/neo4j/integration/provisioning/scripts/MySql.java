package org.neo4j.integration.provisioning.scripts;

import java.io.IOException;

import com.amazonaws.util.IOUtils;
import org.stringtemplate.v4.ST;

import org.neo4j.integration.provisioning.Script;

public class MySql
{
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

    public static Script startupScript()
    {
        return createScript( "/scripts/mysql-startup.sh" );
    }

    public static Script setupDatabaseScript()
    {
        return createScript( "/scripts/setup-db.sql" );
    }

    private static Script createScript(String path)
    {
        return new Script()
        {
            @Override
            public String value() throws IOException
            {
                String script = IOUtils.toString( getClass().getResourceAsStream( path ) );

                ST template = new ST( script );

                for ( Parameters parameter : Parameters.values() )
                {
                    template.add( parameter.name(), parameter.value() );
                }

                return template.render();
            }
        };
    }


}
