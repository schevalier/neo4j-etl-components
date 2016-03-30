package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.sql.exportcsv.io.RowStrategy;

public class Resource
{
    private final String name;
    private final GraphObjectType graphObjectType;
    private final String sql;
    private final ColumnToCsvFieldMappings mappings;
    private final RowStrategy rowStrategy;

    public Resource( String name,
                     GraphObjectType graphObjectType,
                     String sql,
                     ColumnToCsvFieldMappings mappings,
                     RowStrategy rowStrategy )
    {
        this.name = name;
        this.graphObjectType = graphObjectType;
        this.sql = sql;
        this.mappings = mappings;
        this.rowStrategy = rowStrategy;
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

    public RowStrategy rowStrategy()
    {
        return rowStrategy;
    }
}
