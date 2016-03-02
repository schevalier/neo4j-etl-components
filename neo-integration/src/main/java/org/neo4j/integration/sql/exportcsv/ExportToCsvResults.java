package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class ExportToCsvResults implements Iterable<ExportToCsvResults.ExportToCsvResult>
{
    private final Collection<ExportToCsvResult> exportResults;

    public ExportToCsvResults( Collection<ExportToCsvResult> exportResults )
    {
        this.exportResults = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( exportResults, "exportResults"));
    }

    public GraphConfig createGraphConfig()
    {
        Collection<GraphDataConfig> graphDataConfig = new ArrayList<>();

        for ( ExportToCsvResult result : exportResults )
        {
            if ( result.databaseObject() instanceof Table )
            {
                graphDataConfig.add( NodeConfig.builder()
                        .addInputFiles( result.csvFiles() )
                        .addLabel( ((Table) result.databaseObject()).name().simpleName() )
                        .build() );
            }
            else if ( result.databaseObject() instanceof Join )
            {
                graphDataConfig.add( RelationshipConfig.builder()
                        .addInputFiles( result.csvFiles() )
                        .build() );
            }
            else if ( result.databaseObject() instanceof JoinTable )
            {
                graphDataConfig.add( RelationshipConfig.builder()
                        .addInputFiles( result.csvFiles() )
                        .build() );
            }
            else
            {
                throw new IllegalStateException(
                        format( "Unknown database object: %s", result.databaseObject() ) );
            }
        }

        return new GraphConfig( graphDataConfig );
    }

    @Override
    public Iterator<ExportToCsvResult> iterator()
    {
        return exportResults.iterator();
    }

    public static class ExportToCsvResult
    {
        private final DatabaseObject databaseObject;
        private final Collection<Path> csvFiles;

        public ExportToCsvResult( DatabaseObject databaseObject, Collection<Path> csvFiles )
        {
            this.databaseObject = Preconditions.requireNonNull( databaseObject, "databaseObject" );
            this.csvFiles = Collections.unmodifiableCollection(
                    Preconditions.requireNonEmptyCollection( csvFiles, "csvFiles" ) );
        }

        public DatabaseObject databaseObject()
        {
            return databaseObject;
        }

        public Collection<Path> csvFiles()
        {
            return csvFiles;
        }
    }
}
