package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/*
Compares lists of maps, where each map represents part of a join.
A list with one row represents a simple join. A list with more than one row represents a composite key join.
Before comparing the lists, we sort both lists based on SOURCE_COLUMN_TYPE and SOURCE_COLUMN_NAME - we then
compare the first row in each of the sorted lists.
*/
class JoinKeyQueryResultsComparator implements Comparator<List<Map<String, String>>>
{
    @Override
    public int compare( List<Map<String, String>> rows1, List<Map<String, String>> rows2 )
    {
        return new RowComparator().compare( sortRowsReturnFirst( rows1 ), sortRowsReturnFirst( rows2 ) );
    }

    private Map<String, String> sortRowsReturnFirst( List<Map<String, String>> rows )
    {
        return rows.stream().sorted( new RowComparator() ).findFirst().get();
    }

    // Compares two maps, where each map represents part of a join based on SOURCE_COLUMN_TYPE and SOURCE_COLUMN_NAME.
    class RowComparator implements Comparator<Map<String, String>>
    {
        @Override
        public int compare( Map<String, String> o1, Map<String, String> o2 )
        {
            String columnType1 = o1.get( "SOURCE_COLUMN_TYPE" );
            String columnType2 = o2.get( "SOURCE_COLUMN_TYPE" );

            int i = columnType1.compareTo( columnType2 );

            if ( i == 0 )
            {
                String columnName1 = o1.get( "SOURCE_COLUMN_NAME" );
                String columnName2 = o2.get( "SOURCE_COLUMN_NAME" );

                return columnName1.compareTo( columnName2 );
            }
            else
            {
                return i;
            }
        }
    }

}
