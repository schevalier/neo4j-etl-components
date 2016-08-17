package org.neo4j.etl.sql.metadata;

import org.neo4j.etl.sql.RowAccessor;

public enum ColumnValueSelectionStrategy
{
    SelectColumnValue( true )
            {
                @Override
                String selectFrom( RowAccessor row, int rowIndex, String key )
                {
                    return row.getString( key );
                }
            },
    SelectRowIndex( false )
            {
                @Override
                String selectFrom( RowAccessor row, int rowIndex, String key )
                {
                    return String.valueOf( rowIndex );
                }
            };

    private final boolean allowAddToSelectStatement;

    ColumnValueSelectionStrategy( boolean allowAddToSelectStatement )
    {
        this.allowAddToSelectStatement = allowAddToSelectStatement;
    }

    abstract String selectFrom( RowAccessor row, int rowIndex, String key );

    public boolean allowAddToSelectStatement()
    {
        return allowAddToSelectStatement;
    }
}
