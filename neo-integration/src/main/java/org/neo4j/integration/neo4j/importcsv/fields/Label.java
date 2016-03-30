package org.neo4j.integration.neo4j.importcsv.fields;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.neo4j.integration.neo4j.importcsv.config.Formatter;

class Label implements CsvField
{
    private final String value = ":LABEL";

    @Override
    public String value( Formatter formatter )
    {
        return value;
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "type", getClass().getSimpleName() );

        return root;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Label label = (Label) o;

        return value.equals( label.value );

    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
