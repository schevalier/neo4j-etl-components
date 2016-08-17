package org.neo4j.etl.provisioning;

public interface Server extends AutoCloseable
{
    String ipAddress();
}
