package org.neo4j.ingest.config;

public interface ConfigSupplier
{
    void addConfigTo( ImportConfig.Builder importConfig );
}
