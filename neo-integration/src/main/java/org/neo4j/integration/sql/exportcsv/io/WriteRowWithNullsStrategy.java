package org.neo4j.integration.sql.exportcsv.io;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;

public class WriteRowWithNullsStrategy implements BiPredicate<QueryResults, Collection<Column>>
{
    @Override
    public boolean test( QueryResults results, Collection<Column> columns )
    {
        try
        {

            boolean allowWriteLine = true;
            for ( Column column : columns )
            {
                if ( isKeyColumn( column.type() ) )
                {
                    if ( StringUtils.isEmpty( results.getString( column.alias() ) ) )
                    {
                        allowWriteLine = false;
                        break;
                    }
                }
            }
            return allowWriteLine;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isKeyColumn( ColumnType type )
    {
        return ColumnType.ForeignKey == type || ColumnType.PrimaryKey == type;
    }
}
