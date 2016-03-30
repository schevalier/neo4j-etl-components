package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;

public class CsvResource
{
    private final String name;
    private final GraphObjectType graphObjectType;
    private final String sql;
    private final ColumnToCsvFieldMappings mappings;

    public CsvResource( String name,
                        GraphObjectType graphObjectType,
                        String sql,
                        ColumnToCsvFieldMappings mappings )
    {
        this.name = name;
        this.graphObjectType = graphObjectType;
        this.sql = sql;
        this.mappings = mappings;
    }

    public String name()
    {
        return name;
    }

    public GraphObjectType graphObjectType()
    {
        return graphObjectType;
    }

    public String sql()
    {
        return sql;
    }

    public ColumnToCsvFieldMappings mappings()
    {
        return mappings;
    }
}
