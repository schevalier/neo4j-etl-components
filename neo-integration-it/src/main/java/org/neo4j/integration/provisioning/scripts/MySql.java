package org.neo4j.integration.provisioning.scripts;

import java.io.IOException;

import com.amazonaws.util.IOUtils;
import org.stringtemplate.v4.ST;

import org.neo4j.integration.provisioning.StartupScript;

public class MySql implements StartupScript
{
    public enum Parameters
    {
        DBRootPassword(  "xsjhdcfhsd" ), DBUser( "neo" ), DBPassword( "sadsa786da8s7f6" );

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

    @Override
    public String value() throws IOException
    {
        String script = IOUtils.toString( getClass().getResourceAsStream( "/scripts/mysql-startup.sh" ) );

        ST template = new ST( script );

        for ( Parameters parameter : Parameters.values() )
        {
            template.add( parameter.name(), parameter.value() );
        }

        return template.render();
    }
}
