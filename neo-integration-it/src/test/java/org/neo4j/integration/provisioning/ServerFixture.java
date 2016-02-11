package org.neo4j.integration.provisioning;

import java.nio.file.Path;

import org.neo4j.integration.provisioning.environments.Vagrant;
import org.neo4j.integration.util.LazyResource;
import org.neo4j.integration.util.Resource;

public class ServerFixture
{
    public static Resource<Server> server( Path directory, Script script )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<Server>()
        {
            @Override
            public Server create() throws Exception
            {
                return new Vagrant( directory ).createServer( script );
            }

            @Override
            public void destroy( Server server ) throws Exception
            {
                server.close();
            }
        } );
    }
}
