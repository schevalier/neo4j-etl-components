package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CsvResources implements Iterable<CsvResource>
{
    public static CsvResources fromJson( JsonNode root )
    {
        CsvResources csvResources = new CsvResources();

        ArrayNode array = (ArrayNode) root;
        for ( JsonNode csvResource : array )
        {
            csvResources.add( CsvResource.fromJson( csvResource ) );
        }

        return csvResources;
    }

    private final Collection<CsvResource> csvResources = new ArrayList<>();

    public CsvResources add( CsvResource csvResource )
    {
        csvResources.add( csvResource );
        return this;
    }

    @Override
    public Iterator<CsvResource> iterator()
    {
        return csvResources.iterator();
    }

    public JsonNode toJson()
    {
        ArrayNode root = JsonNodeFactory.instance.arrayNode();

        for ( CsvResource csvResource : csvResources )
        {
            root.add( csvResource.toJson() );
        }

        return root;
    }
}
