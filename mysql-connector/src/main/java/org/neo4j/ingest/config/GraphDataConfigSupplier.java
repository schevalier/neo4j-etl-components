package org.neo4j.ingest.config;

public interface GraphDataConfigSupplier
{
    void addGraphDataConfigTo( ImportConfig.Builder importConfig );
}
