package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;

class ColumnToCsvFieldMappingsBuilder implements ColumnToCsvFieldMappings.Builder
{
    private final Map<Column, CsvField> mappings = new LinkedHashMap<>();

    @Override
    public ColumnToCsvFieldMappings.Builder add( Column from, CsvField to )
    {
        mappings.put( from, to );
        return this;
    }

    @Override
    public ColumnToCsvFieldMappings build()
    {
        return new ColumnToCsvFieldMappings( mappings );
    }
}
