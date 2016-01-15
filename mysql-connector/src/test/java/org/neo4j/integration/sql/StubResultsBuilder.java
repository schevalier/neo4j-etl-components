package org.neo4j.integration.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

class StubResultsBuilder implements StubResults.Builder.SetColumns, StubResults.Builder
{
    private final List<Map<String, String>> rows = new ArrayList<>();
    private List<String> columns;

    @Override
    public StubResults.Builder columns( String... columns )
    {
        this.columns = asList( columns );
        return this;
    }

    @Override
    public StubResults.Builder addRow( String... values )
    {
        if ( values.length != columns.size() )
        {
            throw new IllegalArgumentException(
                    format( "Expected %s values, found %s", columns.size(), values.length ) );
        }

        Map<String, String> row = new HashMap<>();

        for ( int i = 0; i < columns.size(); i++ )
        {
            row.put( columns.get( i ), values[i] );
        }

        rows.add( row );

        return this;
    }

    @Override
    public Results build()
    {
        return new StubResults( rows );
    }
}
