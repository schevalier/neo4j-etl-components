package org.neo4j.integration.sql;

import java.util.List;
import java.util.Map;

public class StubResults implements Results
{
    public static Builder.SetColumns builder()
    {
        return new StubResultsBuilder();
    }

    private final List<Map<String, String>> rows;
    private int currentRowIndex = -1;

    StubResults( List<Map<String, String>> rows )
    {
        this.rows = rows;
    }

    public boolean next() throws Exception
    {
        currentRowIndex++;

        return currentRowIndex < rows.size();
    }

    @Override
    public String getString( String columnLabel ) throws Exception
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
            Builder columns(String... columns);
        }

        Builder addRow(String... rows);

        Results build();
    }
}
