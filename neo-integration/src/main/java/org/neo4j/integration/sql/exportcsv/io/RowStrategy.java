package org.neo4j.integration.sql.exportcsv.io;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;

enum RowStrategy
{
    WriteRowWithNullKey
            {
                @Override
                public boolean isWriteableRow( RowAccessor row, int rowIndex, Column[] columns )
                {
                    return true;
                }
            },
    IgnoreRowWithNullKey
            {
                @Override
                public boolean isWriteableRow( RowAccessor row, int rowIndex, Column[] columns )
                {
                    boolean allowWriteLine = true;
                    for ( Column column : columns )
                    {
                        if ( isKeyColumn( column.role() ) )
                        {
                            if ( StringUtils.isEmpty( column.selectFrom( row, rowIndex ) ) )
                            {
                                allowWriteLine = false;
                                break;
                            }
                        }
                    }
                    return allowWriteLine;
                }
            };

    public static RowStrategy select( GraphObjectType graphObjectType )
    {
        if ( graphObjectType == GraphObjectType.Node )
        {
            return WriteRowWithNullKey;
        }
        else
        {
            return IgnoreRowWithNullKey;
        }
    }

    private static boolean isKeyColumn( ColumnRole role )
    {
        return role == ColumnRole.ForeignKey || role == ColumnRole.PrimaryKey;
    }

    public abstract boolean isWriteableRow( RowAccessor row, int rowIndex, Column[] columns );
}
