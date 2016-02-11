package org.neo4j.integration.provisioning;

public interface ServerFactory
{
    Server createServer( Script script ) throws Exception;
}
