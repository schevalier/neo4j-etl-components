package org.neo4j.integration.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StubQueryResults implements QueryResults
{
    public static Builder.SetColumns builder()
    {
        return new StubResultsBuilder();
    }

    private final List<Map<String, String>> rows;
    private int currentRowIndex = -1;

    StubQueryResults( List<Map<String, String>> rows )
    {
        this.rows = rows;
    }

    public boolean next() throws Exception
    {
        currentRowIndex++;

        return currentRowIndex < rows.size();
    }

    @Override
    public Stream<Map<String, String>> streamOfResults( List<String> columnLabels )
    {
        List<Map<String, String>> listOfResults = new ArrayList<>();
        try
        {
            while ( next() )
            {
                Map<String, String> map = new HashMap<>();
                for ( String columnLabel : columnLabels )
                {
                    map.put( columnLabel, getString( columnLabel ) );

                }
                listOfResults.add( map );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return listOfResults.stream();
    }

    @Override
    public String getString( String columnLabel )
    {
        return rows.get( currentRowIndex ).get( columnLabel );
    }

    @Override
    public void close() throws Exception
    {
        // Do nothing
    }

    public interface Builder
    {
        interface SetColumns
        {
            Builder columns( String... columns );
        }

        Builder addRow( String... rows );

        QueryResults build();
    }
}
