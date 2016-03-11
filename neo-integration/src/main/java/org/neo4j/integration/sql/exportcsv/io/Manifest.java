package org.neo4j.integration.sql.exportcsv.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;

public class Manifest
{
    private final Collection<ManifestEntry> manifestEntries = new ArrayList<>();

    public Manifest add( ManifestEntry manifestEntry )
    {
        this.manifestEntries.add( manifestEntry );
        return this;
    }

    public Collection<CsvFiles> csvFilesForNodes()
    {
        return csvFilesForGraphObject( GraphObjectType.Node );
    }

    public Collection<CsvFiles> csvFilesForRelationships()
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
