package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.Preconditions;

public class ColumnToCsvFieldMappings
{
    public static Builder builder()
    {
        return new ColumnToCsvFieldMappingsBuilder();
    }

    private final Collection<ColumnToCsvFieldMapping> mappings;

    ColumnToCsvFieldMappings( Collection<ColumnToCsvFieldMapping> mappings )
    {
        this.mappings = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( mappings, "Mappings" ) );
    }

    public Collection<CsvField> fields()
    {
        return mappings.stream().map( ColumnToCsvFieldMapping::field ).collect( Collectors.toList() );
    }

    public Collection<Column> columns()
    {
        return mappings.stream().map( ColumnToCsvFieldMapping::column ).collect( Collectors.toList() );
    }

    public Collection<String> aliasedColumns()
    {
        return columns().stream()
                .map( Column::aliasedColumn )
                .collect( Collectors.toList() );
    }

    public Collection<String> tableNames()
    {
        return columns().stream()
                .map( Column::table )
                .distinct()
                .map( TableName::fullName )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
    }

    public JsonNode toJson()
    {
        ArrayNode root = JsonNodeFactory.instance.arrayNode();

        for ( ColumnToCsvFieldMapping mapping : mappings )
        {
            root.add( mapping.toJson() );
        }

       return root;
    }

    public interface Builder
    {
        Builder add( ColumnToCsvFieldMapping columnToCsvFieldMapping );

        ColumnToCsvFieldMappings build();
    }
}
