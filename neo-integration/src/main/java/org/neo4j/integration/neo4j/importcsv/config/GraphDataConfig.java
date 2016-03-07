package org.neo4j.integration.neo4j.importcsv.config;

import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;

public interface GraphDataConfig
{
    void addTo( ImportConfig.Builder importConfig );
}
