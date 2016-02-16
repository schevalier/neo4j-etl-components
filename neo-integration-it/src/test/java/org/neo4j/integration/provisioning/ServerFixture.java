package org.neo4j.integration.provisioning;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

import org.neo4j.integration.provisioning.platforms.Aws;
import org.neo4j.integration.provisioning.platforms.Vagrant;
import org.neo4j.integration.util.LazyResource;
import org.neo4j.integration.util.Resource;
import org.neo4j.integration.util.SystemProperties;

public class ServerFixture
{
    public static Resource<Server> server( String description, int port, Script script, Path directory )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<Server>()
        {
            @Override
            public Server create() throws Exception
            {
                String platform = SystemProperties.asOptionalString( "PLATFORM" ).orElse( "local" ).toLowerCase();
                Optional<String> ec2Key = SystemProperties.asOptionalString( "EC2_SSH_KEY" );
                Optional<String> vagrantBoxUri = SystemProperties.asOptionalString( "VAGRANT_BOX_URI" );

                if ( platform.equals( "aws" ) && ec2Key.isPresent() )
                {
                    return new Aws( description, ec2Key.get(), port ).createServer( script );
                }
                else if ( vagrantBoxUri.isPresent() && !vagrantBoxUri.get().isEmpty() )
                {
                    return new Vagrant( URI.create( vagrantBoxUri.get() ), directory ).createServer( script );
                }
                else
                {
                    return new Vagrant( directory ).createServer( script );
                }
            }

            @Override
            public void destroy( Server server ) throws Exception
            {
                server.close();
            }
        } );
    }
}
