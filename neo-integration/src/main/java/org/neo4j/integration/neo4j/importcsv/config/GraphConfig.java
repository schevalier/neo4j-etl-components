package org.neo4j.integration.neo4j.importcsv.config;

import java.util.Collection;

public class GraphConfig implements GraphDataConfig
{
    private final Collection<GraphDataConfig> configSuppliers;

    public GraphConfig( Collection<GraphDataConfig> configSuppliers )
    {
        this.configSuppliers = configSuppliers;
    }

    @Override
    public void addTo( ImportConfig.Builder importConfig )
    {
        for ( GraphDataConfig configSupplier : configSuppliers )
        {
            configSupplier.addTo( importConfig );
        }
    }
}
