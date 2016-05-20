package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.sql.RowAccessor;

public enum ColumnValueSelectionStrategy
{
    SelectColumnValue
            {
                @Override
                String selectFrom( RowAccessor row, int rowIndex, String key )
                {
                    return row.getString( key );
                }
            },
    SelectRowIndex
            {
                @Override
                String selectFrom( RowAccessor row, int rowIndex, String key )
                {
                    return String.valueOf( rowIndex );
                }
            };

    abstract String selectFrom( RowAccessor row, int rowIndex, String key );
}
