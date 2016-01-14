package org.neo4j.integration.mysql.exportcsv.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.integration.mysql.exportcsv.metadata.Column;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;

public class ColumnToCsvFieldMappings
{
    private final Map<Column, CsvField> mappings = new LinkedHashMap<>();

    public void add( Column from, CsvField to )
    {
        mappings.put( from, to );
    }

    public Collection<CsvField> fields()
    {
        return mappings.values();
    }

    public Collection<String> columns()
    {
        return mappings.keySet().stream().map( Column::name ).collect( Collectors.toList() );
    }

    public Collection<String> tableNames()
    {
        return mappings.keySet().stream().map( c -> c.table().fullName() ).collect( Collectors.toSet() );
    }
}
