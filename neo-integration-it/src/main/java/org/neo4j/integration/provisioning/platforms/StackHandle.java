package org.neo4j.integration.provisioning.platforms;

import java.util.logging.Level;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;

import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.util.Loggers;

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
        Loggers.Default.log( Level.SEVERE, "Not deleting until the stack problem is fixed" );
//        cloudFormation.deleteStack( new DeleteStackRequest().withStackName( stackName ) );
    }
}
