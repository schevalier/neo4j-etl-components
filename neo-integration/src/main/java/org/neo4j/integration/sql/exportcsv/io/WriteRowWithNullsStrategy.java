package org.neo4j.integration.sql.exportcsv.io;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.util.BiPredicate;

public class WriteRowWithNullsStrategy implements BiPredicate<RowAccessor, Collection<Column>>
{
    @Override
    public boolean test( RowAccessor row, Collection<Column> columns ) throws Exception
    {
        boolean allowWriteLine = true;
        for ( Column column : columns )
        {
            if ( isKeyColumn( column.type() ) )
            {
                if ( StringUtils.isEmpty( row.getString( column.alias() ) ) )
                {
                    allowWriteLine = false;
                    break;
                }
            }
        }
        return allowWriteLine;
    }

    private boolean isKeyColumn( ColumnType type )
    {
        return ColumnType.ForeignKey == type || ColumnType.PrimaryKey == type;
    }
}
