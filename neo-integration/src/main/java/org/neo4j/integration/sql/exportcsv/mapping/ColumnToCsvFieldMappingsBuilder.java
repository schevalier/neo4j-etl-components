package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.ArrayList;
import java.util.Collection;

class ColumnToCsvFieldMappingsBuilder implements ColumnToCsvFieldMappings.Builder
{
    private final Collection<ColumnToCsvFieldMapping> mappings = new ArrayList<>();

    @Override
    public ColumnToCsvFieldMappings.Builder add( ColumnToCsvFieldMapping columnToCsvFieldMapping )
    {
        mappings.add( columnToCsvFieldMapping );
        return this;
    }

    @Override
    public ColumnToCsvFieldMappings build()
    {
        return new ColumnToCsvFieldMappings( mappings );
    }
}
