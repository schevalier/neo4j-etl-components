package org.neo4j.integration.provisioning;

public interface ServerFactory
{
    Server createServer( StartupScript script ) throws Exception;
}
