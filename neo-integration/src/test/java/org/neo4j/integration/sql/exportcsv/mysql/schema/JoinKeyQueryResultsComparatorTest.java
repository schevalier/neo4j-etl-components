package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.stream.Collectors;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;

import static org.junit.Assert.assertThat;

public class JoinKeyQueryResultsComparatorTest
{
    @Test
    public void shouldCompareTwoSingleElementListsBySourceColumnType()
    {
        // given
        QueryResults rows1 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "PrimaryKey", "id" )
                .build();

        QueryResults rows2 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "ForeignKey", "id" )
                .build();

        // when
        int result = new JoinKeyQueryResultsComparator().compare(
                rows1.stream().collect( Collectors.toList() ),
                rows2.stream().collect( Collectors.toList() ) );

        // then
        assertThat( result, firstElementGreaterThanSecond() );
    }

    @Test
    public void shouldCompareTwoSingleElementListsWithSameSourceColumnTypeBySourceColumnName()
    {
        // given
        QueryResults rows1 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "ForeignKey", "Z" )
                .build();

        QueryResults rows2 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "ForeignKey", "A" )
                .build();

        // when
        int result = new JoinKeyQueryResultsComparator().compare(
                rows1.stream().collect( Collectors.toList() ),
                rows2.stream().collect( Collectors.toList() ) );

        // then
        assertThat( result, firstElementGreaterThanSecond() );
    }

    @Test
    public void shouldCompareTwoMultiElementListsBySourceColumnType()
    {
        // given
        QueryResults rows1 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "PrimaryKey", "A" )
                .build();

        QueryResults rows2 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "PrimaryKey", "Z" )
                .addRow( "ForeignKey", "Z" )
                .build();

        // when
        int result = new JoinKeyQueryResultsComparator().compare(
                rows1.stream().collect( Collectors.toList() ),
                rows2.stream().collect( Collectors.toList() ) );

        // then
        assertThat( result, firstElementGreaterThanSecond() );
    }

    @Test
    public void shouldCompareTwoMultiElementListsWithSameSourceColumnTypeBySourceColumnName()
    {
        // given
        QueryResults rows1 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "ForeignKey", "Z" )
                .build();

        QueryResults rows2 = StubQueryResults.builder().columns( "SOURCE_COLUMN_TYPE", "SOURCE_COLUMN_NAME" )
                .addRow( "ForeignKey", "A" )
                .addRow( "ForeignKey", "Z" )
                .build();

        // when
        int result = new JoinKeyQueryResultsComparator().compare(
                rows1.stream().collect( Collectors.toList() ),
                rows2.stream().collect( Collectors.toList() ) );

        // then
        assertThat( result, firstElementGreaterThanSecond() );
    }

    private TypeSafeMatcher<Integer> firstElementGreaterThanSecond()
    {
        return new TypeSafeMatcher<Integer>()
        {
            @Override
            protected boolean matchesSafely( Integer integer )
            {
                return integer > 1;
            }

            @Override
            public void describeTo( Description description )
            {

            }
        };
    }
}
