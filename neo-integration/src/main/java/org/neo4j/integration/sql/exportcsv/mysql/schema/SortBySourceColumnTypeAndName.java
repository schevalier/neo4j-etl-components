package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

class SortBySourceColumnTypeAndName implements Comparator<List<Map<String, String>>>
{
    @Override
    public int compare( List<Map<String, String>> o1, List<Map<String, String>> o2 )
    {
        Map<String, String> firstElementRow0 = o1.stream().sorted( new RowComparator() ).findFirst().get();
        Map<String, String> secondElementRow0 = o2.stream().sorted( new RowComparator() ).findFirst().get();

        return new RowComparator().compare( firstElementRow0, secondElementRow0 );
    }

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
