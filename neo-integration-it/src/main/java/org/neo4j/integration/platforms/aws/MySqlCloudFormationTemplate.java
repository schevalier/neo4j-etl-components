package org.neo4j.integration.platforms.aws;

import java.io.IOException;
import java.util.Optional;

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

import org.neo4j.integration.platforms.StartupScript;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class MySqlCloudFormationTemplate
{
    private enum Parameters
    {
        KeyName, AMI, UserData
    }

    private static final String ON_UPGRADED_SCRIPT = "apt-get -y install python-setuptools\n" +
            "easy_install https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz";
    private static final String IMAGE_ID = "ami-bdfbccca";

    public StackHandle createStack() throws IOException
    {
        String template = IOUtils.toString( getClass().getResourceAsStream( "/mysql-template.json" ) );

        AmazonCloudFormation cloudFormation = new AmazonCloudFormationClient();
        cloudFormation.setRegion( Region.getRegion( Regions.EU_WEST_1 ) );

        String stackName = "mysql-integration-test";

        StartupScript startupScript = new StartupScript(
                "password",
                "neo",
                "password",
                MySqlCloudFormationTemplate.ON_UPGRADED_SCRIPT );

        cloudFormation.createStack( new CreateStackRequest()
                .withStackName( stackName )
                .withCapabilities( Capability.CAPABILITY_IAM )
                .withOnFailure( OnFailure.DELETE )
                .withTemplateBody( template )
                .withParameters(
                        parameter( Parameters.KeyName, "iansrobinson" ),
                        parameter( Parameters.AMI, IMAGE_ID ),
                        parameter( Parameters.UserData, startupScript.value() )
                ) );

        while ( !Thread.currentThread().isInterrupted() )
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
                        return new StackHandle( cloudFormation, stackName );
                    case "FAILED":
                    case "ROLLBACK":
                        throw new IOException(
                                format( "Stack creation failed: %s", stack.get().getStackStatusReason() ) );
                    default:
                        try
                        {
                            Thread.sleep( 5000 );
                        }
                        catch ( InterruptedException e )
                        {
                            Thread.currentThread().interrupt();
                        }
                }
            }
        }

        return new StackHandle( cloudFormation, stackName );
    }

    private Parameter parameter( Parameters key, String value )
    {
        return new Parameter().withParameterKey( key.name() ).withParameterValue( value );
    }

    private static class StackHandle implements AutoCloseable
    {
        private final AmazonCloudFormation cloudFormation;
        private final String stackName;

        private StackHandle( AmazonCloudFormation cloudFormation, String stackName )
        {
            this.cloudFormation = cloudFormation;
            this.stackName = stackName;
        }

        @Override
        public void close() throws Exception
        {
            cloudFormation.deleteStack( new DeleteStackRequest().withStackName( stackName ) );
        }
    }
}
