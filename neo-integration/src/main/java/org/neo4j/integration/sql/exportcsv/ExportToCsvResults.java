package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class ExportToCsvResults implements Iterable<ExportToCsvResults.ExportToCsvResult>
{
    private final Collection<ExportToCsvResult> exportResults;

    public ExportToCsvResults( Collection<ExportToCsvResult> exportResults )
    {
        this.exportResults = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( exportResults, "ExportResults" ) );
    }

    public GraphConfig createGraphConfig()
    {
        Collection<GraphDataConfig> graphDataConfig = new ArrayList<>();

        for ( ExportToCsvResult result : exportResults )
        {
            graphDataConfig.add( result );
        }

        return new GraphConfig( graphDataConfig );
    }

    @Override
    public Iterator<ExportToCsvResult> iterator()
    {
        return exportResults.iterator();
    }

    public static class ExportToCsvResult implements GraphDataConfig
    {
        private final DatabaseObject databaseObject;
        private final Collection<Path> csvFiles;

        public ExportToCsvResult( DatabaseObject databaseObject, Collection<Path> csvFiles )
        {
            this.databaseObject = Preconditions.requireNonNull( databaseObject, "DatabaseObject" );
            this.csvFiles = Collections.unmodifiableCollection(
                    Preconditions.requireNonEmptyCollection( csvFiles, "CsvFiles" ) );
        }

        @Override
        public void addTo( ImportConfig.Builder importConfig )
        {
            if ( databaseObject.isTable() )
            {
                importConfig.addNodeConfig( NodeConfig.builder()
                        .addInputFiles( csvFiles )
                        .addLabel( ((Table) databaseObject).name().simpleName() )
                        .build() );
            }
            else if ( databaseObject.isJoin() || databaseObject.isJoinTable() )
            {
                importConfig.addRelationshipConfig( RelationshipConfig.builder()
                        .addInputFiles( csvFiles )
                        .build() );
            }
            else
            {
                throw new IllegalStateException(
                        format( "Unknown database object: %s", databaseObject ) );
            }
        }
    }
}
