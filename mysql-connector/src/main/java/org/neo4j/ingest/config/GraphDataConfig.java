package org.neo4j.ingest.config;

import java.util.Collection;

public class GraphDataConfig implements GraphDataConfigSupplier
{
    private final Collection<GraphDataConfigSupplier> configSuppliers;

    public GraphDataConfig( Collection<GraphDataConfigSupplier> configSuppliers )
    {
        this.configSuppliers = configSuppliers;
    }


    @Override
    public void addGraphDataConfigTo( ImportConfig.Builder importConfig )
    {
        for ( GraphDataConfigSupplier configSupplier : configSuppliers )
        {
            configSupplier.addGraphDataConfigTo( importConfig );
        }
    }
}
