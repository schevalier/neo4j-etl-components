package org.neo4j.integration.provisioning.platforms;

import org.neo4j.integration.provisioning.Script;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFactory;

public class Local implements ServerFactory
{
    @Override
    public Server createServer( Script script, TestType testType ) throws Exception
    {
        return new Server()
        {
            @Override
            public String ipAddress()
            {
                return "localhost";
            }

            @Override
            public void close() throws Exception
            {
                // Do nothing
            }
        };
    }
}
