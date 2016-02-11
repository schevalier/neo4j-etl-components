package org.neo4j.integration.provisioning;

public interface Server extends AutoCloseable
{
    String ipAddress();
}
