package org.neo4j.integration.provisioning.platforms;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;

import org.neo4j.integration.provisioning.Server;

class StackHandle implements Server
{
    private final String ipAddress;
    private final AmazonCloudFormation cloudFormation;
    private final String stackName;

    StackHandle( String ipAddress, AmazonCloudFormation cloudFormation, String stackName )
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
