package org.neo4j.integration.provisioning.environments;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.Capability;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.OnFailure;
import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.util.IOUtils;

import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFactory;
import org.neo4j.integration.provisioning.StartupScript;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class Aws implements ServerFactory
{
    private enum Parameters
    {
        KeyName, AMI, InstanceDescription, UserData, Port
    }

    private static final String IMAGE_ID = "ami-bdfbccca";

    private final String instanceDescription;
    private final String keyName;
    private final int port;

    public Aws( String instanceDescription, String keyName, int port )
    {
        this.instanceDescription = instanceDescription;
        this.keyName = keyName;
        this.port = port;
    }

    @Override
    public Server createServer( StartupScript script ) throws Exception
    {
        String template = IOUtils.toString( getClass().getResourceAsStream( "/cloudformation-template.json" ) );

        AmazonCloudFormation cloudFormation = new AmazonCloudFormationClient();
        cloudFormation.setRegion( Region.getRegion( Regions.EU_WEST_1 ) );

        String stackName = createStackName();

        cloudFormation.createStack( new CreateStackRequest()
                .withStackName( stackName )
                .withCapabilities( Capability.CAPABILITY_IAM )
                .withOnFailure( OnFailure.DELETE )
                .withTemplateBody( template )
                .withParameters(
                        parameter( Parameters.KeyName, keyName ),
                        parameter( Parameters.AMI, IMAGE_ID ),
                        parameter( Parameters.InstanceDescription, instanceDescription ),
                        parameter( Parameters.UserData, script.value() ),
                        parameter( Parameters.Port, String.valueOf( port ) )
                ) );

        while ( true )
        {
            DescribeStacksResult stacks = cloudFormation.describeStacks(
                    new DescribeStacksRequest().withStackName( stackName ) );

            Optional<Stack> stack = stacks.getStacks().stream().findFirst();

            if ( stack.isPresent() )
            {
                String status = stack.get().getStackStatus();
                Loggers.Default.log().fine( status );

                switch ( status )
                {
                    case "CREATE_COMPLETE":
                        return new StackHandle( publicIpAddress( stack ), cloudFormation, stackName );
                    case "FAILED":
                    case "ROLLBACK":
                        throw new IOException(
                                format( "Stack creation failed: %s", stack.get().getStackStatusReason() ) );
                    default:
                        Thread.sleep( 5000 );
                }
            }
        }
    }

    private String createStackName()
    {
        return format( "%s-%s",
                instanceDescription.toLowerCase().replace( " ", "-" ),
                UUID.randomUUID().toString().substring( 0, 5 ) );
    }

    private String publicIpAddress( Optional<Stack> stack )
    {
        return stack.get().getOutputs().stream()
                .filter( o -> o.getOutputKey().equals( "PublicIpAddress" ) )
                .findFirst()
                .orElseThrow( () -> new IllegalStateException( "Public IP address not available for EC2 instance" ) )
                .getOutputValue();
    }

    private Parameter parameter( Parameters key, String value )
    {
        return new Parameter().withParameterKey( key.name() ).withParameterValue( value );
    }

    private static class StackHandle implements Server
    {
        private final String ipAddress;
        private final AmazonCloudFormation cloudFormation;
        private final String stackName;

        private StackHandle( String ipAddress, AmazonCloudFormation cloudFormation, String stackName )
        {
            this.ipAddress = ipAddress;
            this.cloudFormation = cloudFormation;
            this.stackName = stackName;
        }

        @Override
        public String ipAddress()
        {
            return ipAddress;
        }

        @Override
        public void close() throws Exception
        {
            cloudFormation.deleteStack( new DeleteStackRequest().withStackName( stackName ) );
        }
    }
}
