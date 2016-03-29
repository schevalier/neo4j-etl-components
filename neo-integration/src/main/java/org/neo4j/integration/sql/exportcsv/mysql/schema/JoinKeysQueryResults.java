package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;

import static java.lang.String.format;

class JoinKeysQueryResults
{
    private static final Predicate<Map<String, String>> IS_PRIMARY_KEY =
            row -> ColumnType.PrimaryKey.name().equalsIgnoreCase( row.get( "SOURCE_COLUMN_TYPE" ) );
    private static final Predicate<Map<String, String>> IS_FOREIGN_KEY =
            row -> ColumnType.ForeignKey.name().equalsIgnoreCase( row.get( "SOURCE_COLUMN_TYPE" ) );

    private final List<JoinKeyQueryResults> primaryKeyQueryResults;
    private final List<JoinKeyQueryResults> foreignKeyQueryResults;

    JoinKeysQueryResults( List<Map<String, String>> rows )
    {
        this.primaryKeyQueryResults = createListOfJoinKeyQueryResultsByTargetTable( rows, IS_PRIMARY_KEY );
        this.foreignKeyQueryResults = createListOfJoinKeyQueryResultsByTargetTable( rows, IS_FOREIGN_KEY );

        if ( primaryKeyQueryResults.size() > 1 )
        {
            throw new IllegalStateException( "Found more than 1 primary key" );
        }

        if ( primaryKeyQueryResults.size() + foreignKeyQueryResults.size() != 2 )
        {
            throw new IllegalStateException(
                    format( "Unable to find 2 keys (found %s primary key(s) and %s foreign key(s))",
                            primaryKeyQueryResults.size(),
                            foreignKeyQueryResults.size() ) );
        }
    }

    Join createJoin()
    {
        if ( primaryKeyQueryResults.isEmpty() )
        {
            // Creates a join between foreign keys – as might be found in a join table
            return new Join(
                    foreignKeyQueryResults.get( 0 ).createJoinKey(),
                    foreignKeyQueryResults.get( 1 ).createJoinKey() );
        }
        else
        {
            // Creates a Join whose first column represents the primary key in the join, and whose second
            // column represents the foreign key.
            return new Join(
                    primaryKeyQueryResults.get( 0 ).createJoinKey(),
                    foreignKeyQueryResults.get( 0 ).createJoinKey() );
        }
    }

    private List<JoinKeyQueryResults> createListOfJoinKeyQueryResultsByTargetTable(
            List<Map<String, String>> rows,
            Predicate<Map<String, String>> keyTypePredicate )
    {
        return rows.stream()
                .filter( keyTypePredicate )
                .collect( Collectors.groupingBy( row -> row.get( "TARGET_TABLE_NAME" ) ) )
                .values().stream()
                .sorted( new JoinKeyQueryResultsComparator() )
                .map( JoinKeyQueryResults::new )
                .collect( Collectors.toList() );
    }
}
