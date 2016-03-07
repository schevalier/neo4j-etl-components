package org.neo4j.integration.sql.exportcsv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.util.Preconditions;

public class ExportToCsvResults
{
    private final Collection<GraphDataConfig> exportResults;

    public ExportToCsvResults( Collection<GraphDataConfig> exportResults )
    {
        this.exportResults = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( exportResults, "ExportResults" ) );
    }

    public GraphConfig createGraphConfig()
    {
        Collection<GraphDataConfig> graphDataConfig = new ArrayList<>();

        for ( GraphDataConfig result : exportResults )
        {
            graphDataConfig.add( result );
        }

        return new GraphConfig( graphDataConfig );
    }
}
