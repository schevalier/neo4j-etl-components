package org.neo4j.integration.platforms;

import java.io.File;

import org.junit.Test;

public class VagrantTest
{
    @Test
    public void shouldStartVagrantBox() throws Exception
    {
        StartupScript startupScript = new StartupScript( "password", "neo", "password" );

        String ip = new Vagrant().up(
                new File( "/Users/iansrobinson/Desktop/integration" ),
                startupScript.value() );

        System.out.println( ip );
    }

    @Test
    public void shouldDestroyVagrantBox() throws Exception
    {
        new Vagrant().destroy(
                new File( "/Users/iansrobinson/Desktop/integration" ) );
    }
}


