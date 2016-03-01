package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.sql.metadata.Column;

class ColumnToCsvFieldMappingsBuilder implements ColumnToCsvFieldMappings.Builder
{
    private final Map<Column, CsvField> mappings = new LinkedHashMap<>();

    @Override
    public ColumnToCsvFieldMappings.Builder add( Column from, CsvField transformsTo )
    {
        mappings.put( from, transformsTo );
        return this;
    }

    @Override
    public ColumnToCsvFieldMappings build()
    {
        return new ColumnToCsvFieldMappings( mappings );
    }
}
