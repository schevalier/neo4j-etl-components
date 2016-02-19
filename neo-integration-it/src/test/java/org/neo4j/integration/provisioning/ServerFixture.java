package org.neo4j.integration.provisioning;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

import org.neo4j.integration.provisioning.platforms.Aws;
import org.neo4j.integration.provisioning.platforms.Local;
import org.neo4j.integration.provisioning.platforms.Vagrant;
import org.neo4j.integration.util.EnvironmentVariables;
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
                String platform = systemPropertyOrEnvironmentVariable( "PLATFORM" ).orElse( "vagrant" ).toLowerCase();
                Optional<String> ec2Key = systemPropertyOrEnvironmentVariable( "EC2_SSH_KEY" );
                Optional<String> vagrantBoxUri = systemPropertyOrEnvironmentVariable( "VAGRANT_BOX_URI" );

                if ( platform.equalsIgnoreCase( "local" ) )
                {
                    return new Local().createServer( script );
                }
                else if ( platform.equalsIgnoreCase( "aws" ) && ec2Key.isPresent() )
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

    private static Optional<String> systemPropertyOrEnvironmentVariable( String key )
    {
        Optional<String> value = SystemProperties.asOptionalString( key );
        return value.isPresent() ? value : EnvironmentVariables.asOptionalString( key );
    }
}
