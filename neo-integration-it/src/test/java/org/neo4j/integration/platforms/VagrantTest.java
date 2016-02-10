package org.neo4j.integration.platforms;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

public class VagrantTest
{
    @Test
    @Ignore
    public void shouldStartVagrantBox() throws Exception
    {
        StartupScript startupScript = new StartupScript( "password", "neo", "password" );

        Vagrant.VagrantHandle handle = new Vagrant(  ).up(
                Paths.get( "/Users/iansrobinson/Desktop/integration" ),
                startupScript.value() );

        System.out.println( handle.ipAddress() );
    }

    @Test
    @Ignore
    public void shouldDestroyVagrantBox() throws Exception
    {
        new Vagrant(  ).destroy(
                Paths.get( "/Users/iansrobinson/Desktop/integration" ) );
    }
}


