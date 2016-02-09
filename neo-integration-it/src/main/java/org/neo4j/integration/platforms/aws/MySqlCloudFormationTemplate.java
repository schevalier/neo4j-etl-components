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

import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class MySqlCloudFormationTemplate
{
    public static final String KEY_NAME = "KeyName";
    public static final String AMI = "AMI";
    public static final String DB_NAME = "DBName";
    public static final String DB_USER = "DBUser";
    public static final String DB_PASSWORD = "DBPassword";
    public static final String DB_ROOT_PASSWORD = "DBRootPassword";

    private static final String IMAGE_ID = "ami-bdfbccca";

    public AutoCloseable provisionStack() throws IOException
    {
        String template = IOUtils.toString( getClass().getResourceAsStream( "/mysql-template.json" ) );

        AmazonCloudFormation cloudFormation = new AmazonCloudFormationClient();
        cloudFormation.setRegion( Region.getRegion( Regions.EU_WEST_1 ) );

        String stackName = "mysql-integration-test";

        cloudFormation.createStack( new CreateStackRequest()
                .withStackName( stackName )
                .withCapabilities( Capability.CAPABILITY_IAM )
                .withOnFailure( OnFailure.DELETE )
                .withTemplateBody( template )
                .withParameters(
                        new Parameter().withParameterKey( KEY_NAME ).withParameterValue( "iansrobinson" ),
                        new Parameter().withParameterKey( AMI ).withParameterValue( IMAGE_ID ),
                        new Parameter().withParameterKey( DB_NAME ).withParameterValue( "NeoTestDatabase" ),
                        new Parameter().withParameterKey( DB_ROOT_PASSWORD ).withParameterValue( "password" ),
                        new Parameter().withParameterKey( DB_USER ).withParameterValue( "neo" ),
                        new Parameter().withParameterKey( DB_PASSWORD ).withParameterValue( "password" )
                ) );

        while ( !Thread.currentThread().isInterrupted() )
        {
            DescribeStacksResult stacks = cloudFormation.describeStacks(
                    new DescribeStacksRequest().withStackName( stackName ) );

            Optional<Stack> stack = stacks.getStacks().stream().findFirst();

            if ( stack.isPresent() )
            {
                String status = stack.get().getStackStatus();
                Loggers.Default.log().info( status );

                switch ( status )
                {
                    case "CREATE_COMPLETE":
                        return new StackedRequestHandler( cloudFormation, stackName );
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

        return new StackedRequestHandler( cloudFormation, stackName );
    }

    private static class StackedRequestHandler implements AutoCloseable
    {
        private final AmazonCloudFormation cloudFormation;
        private final String stackName;

        private StackedRequestHandler( AmazonCloudFormation cloudFormation, String stackName )
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
