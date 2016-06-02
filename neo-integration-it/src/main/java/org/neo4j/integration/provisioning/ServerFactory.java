package org.neo4j.integration.provisioning;

import org.neo4j.integration.provisioning.platforms.TestType;

public interface ServerFactory
{
    Server createServer( Script script, TestType testType ) throws Exception;
}
