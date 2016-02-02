package org.neo4j.integration;

import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResult;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class SqlToGraphConfigMapper
{
    private final ExportToCsvResults exportResults;

    public SqlToGraphConfigMapper( ExportToCsvResults exportResults )
    {
        this.exportResults = Preconditions.requireNonNull( exportResults, "exportResults" );
    }

    public GraphConfig createGraphConfig()
    {
        Collection<GraphDataConfig> graphDataConfig = new ArrayList<>();

        for ( ExportToCsvResult exportResult : exportResults )
        {
            if ( exportResult.databaseObject() instanceof Table )
            {
                graphDataConfig.add( NodeConfig.builder()
                        .addInputFiles( exportResult.csvFiles() )
                        .addLabel( ((Table) exportResult.databaseObject()).name().simpleName() )
                        .build() );
            }
            else if ( exportResult.databaseObject() instanceof Join )
            {
                graphDataConfig.add( RelationshipConfig.builder()
                        .addInputFiles( exportResult.csvFiles() )
                        .build() );
            }
            else
            {
                throw new IllegalStateException(
                        format( "Unknown database object: %s", exportResult.databaseObject() ) );
            }
        }

        return new GraphConfig( graphDataConfig );
    }
}
