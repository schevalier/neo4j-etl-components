package org.neo4j.integration.sql.exportcsv.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.sql.metadata.Column;

public class ColumnToCsvFieldMapping
{
    public static ColumnToCsvFieldMapping fromJson( JsonNode root )
    {
        return new ColumnToCsvFieldMapping(
                Column.fromJson( root.path( "column" ) ),
                CsvField.fromJson( root.path( "field" ) ) );
    }

    private final Column column;
    private final CsvField field;

    public ColumnToCsvFieldMapping( Column column, CsvField field )
    {
        this.column = column;
        this.field = field;
    }

    public Column column()
    {
        return column;
    }

    public CsvField field()
    {
        return field;
    }

    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.set( "column", column.toJson() );
        root.set( "field", field.toJson() );

        return root;
    }
}
