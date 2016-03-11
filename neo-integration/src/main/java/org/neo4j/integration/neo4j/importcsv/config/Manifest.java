package org.neo4j.integration.neo4j.importcsv.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class Manifest
{
    private final Collection<ManifestEntry> manifestEntries = new ArrayList<>();

    public Manifest add( ManifestEntry manifestEntry )
    {
        this.manifestEntries.add( manifestEntry );
        return this;
    }

    public void addNodesAndRelationshipsToBuilder( ImportConfig.Builder builder )
    {
        csvFilesForNodes().stream()
                .forEach(
                        csvFiles -> builder.addNodeConfig(
                                NodeConfig.builder().addInputFiles( csvFiles.asCollection() ).build() ) );
        csvFilesForRelationships().stream()
                .forEach(
                        csvFiles -> builder.addRelationshipConfig(
                                RelationshipConfig.builder().addInputFiles( csvFiles.asCollection() ).build() ) );
    }

    private Collection<CsvFiles> csvFilesForNodes()
    {
        return csvFilesForGraphObject( GraphObjectType.Node );
    }

    private Collection<CsvFiles> csvFilesForRelationships()
    {
        return csvFilesForGraphObject( GraphObjectType.Relationship );
    }

    private Collection<CsvFiles> csvFilesForGraphObject( GraphObjectType graphObjectType )
    {
        return this.manifestEntries.stream()
                .filter( manifestEntry -> graphObjectType == manifestEntry.graphObjectType() )
                .map( ManifestEntry::csvFiles )
                .collect( Collectors.toList() );
    }
}
