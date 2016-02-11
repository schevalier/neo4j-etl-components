package org.neo4j.integration.provisioning.environments;

import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.scripts.MySql;

public class AwsTest
{
    @Test
    @Ignore
    public void shouldStartCloudFormationStack() throws Exception
    {
        // given
        Aws template = new Aws( "MySQL Database Server", "iansrobinson", 3306 );
        Server server = template.createServer( MySql.startupScript() );

        System.out.println(server.ipAddress());
    }
}
