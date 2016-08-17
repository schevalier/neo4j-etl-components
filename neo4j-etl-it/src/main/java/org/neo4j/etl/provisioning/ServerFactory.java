package org.neo4j.etl.provisioning;

import org.neo4j.etl.provisioning.platforms.TestType;

public interface ServerFactory
{
    Server createServer( Script script, TestType testType ) throws Exception;
}
