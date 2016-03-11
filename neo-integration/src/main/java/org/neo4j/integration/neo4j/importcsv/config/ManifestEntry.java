package org.neo4j.integration.neo4j.importcsv.config;

public class ManifestEntry
{
    private final GraphObjectType graphObjectType;
    private final CsvFiles csvFiles;

    public ManifestEntry( GraphObjectType graphObjectType, CsvFiles csvFiles )
    {
        this.graphObjectType = graphObjectType;
        this.csvFiles = csvFiles;
    }

    public GraphObjectType graphObjectType()
    {
        return graphObjectType;
    }

    public CsvFiles csvFiles()
    {
        return csvFiles;
    }
}
