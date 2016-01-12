package org.neo4j.integration.neo4j.importcsv.config;

public interface GraphDataConfigSupplier
{
    void addGraphDataConfigTo( ImportConfig.Builder importConfig );
}
