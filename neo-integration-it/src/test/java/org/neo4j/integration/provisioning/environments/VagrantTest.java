package org.neo4j.integration.provisioning.environments;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.scripts.MySql;

public class VagrantTest
{
    @Test
    @Ignore
    public void shouldStartVagrantBox() throws Exception
    {
        Path directory = Paths.get( "/Users/iansrobinson/Desktop/neo-mysql" );
        Server server = new Vagrant( directory ).createServer( MySql.startupScript() );

        System.out.println( server.ipAddress() );
    }

    @Test
    @Ignore
    public void shouldDestroyVagrantBox() throws Exception
    {
        new Vagrant( Paths.get( "/Users/iansrobinson/Desktop/neo-mysql" ) ).destroy();
    }
}


