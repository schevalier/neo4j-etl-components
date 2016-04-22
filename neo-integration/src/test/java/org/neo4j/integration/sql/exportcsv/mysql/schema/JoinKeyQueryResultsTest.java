package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.stream.Collectors;

import org.junit.Test;

import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.metadata.JoinKey;

import static org.junit.Assert.assertEquals;

public class JoinKeyQueryResultsTest
{
    @Test
    public void shouldCreateSimpleJoinKeyFromSingleRowList()
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "SOURCE_TABLE_SCHEMA",
                        "SOURCE_TABLE_NAME",
                        "SOURCE_COLUMN_NAME",
                        "SOURCE_COLUMN_TYPE",
                        "TARGET_TABLE_SCHEMA",
                        "TARGET_TABLE_NAME",
                        "TARGET_COLUMN_NAME",
                        "TARGET_COLUMN_TYPE" )
                .addRow( "test", "Person", "addressId", "ForeignKey", "test", "Address", "id", "PrimaryKey" )
                .build();

        JoinKeyQueryResults joinKeyQueryResults =
                new JoinKeyQueryResults( results.stream().collect( Collectors.toList() ) );

        // when
        JoinKey joinKey = joinKeyQueryResults.createJoinKey();

        // then
        assertEquals( "test.Person.addressId", joinKey.sourceColumn().name() );
        assertEquals( "test.Address.id", joinKey.targetColumn().name() );
    }

    @Test
    public void shouldCreateCompositeJoinKeyFromSingleRowList()
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "SOURCE_TABLE_SCHEMA",
                        "SOURCE_TABLE_NAME",
                        "SOURCE_COLUMN_NAME",
                        "SOURCE_COLUMN_TYPE",
                        "TARGET_TABLE_SCHEMA",
                        "TARGET_TABLE_NAME",
                        "TARGET_COLUMN_NAME",
                        "TARGET_COLUMN_TYPE" )
                .addRow( "test", "Author_Publisher", "author_first_name", "ForeignKey", "test", "Author",
                        "first_name", "PrimaryKey" )
                .addRow( "test", "Author_Publisher", "author_last_name", "ForeignKey", "test", "Author", "last_name",
                        "PrimaryKey" )
                .build();

        JoinKeyQueryResults joinKeyQueryResults =
                new JoinKeyQueryResults( results.stream().collect( Collectors.toList() ) );

        // when
        JoinKey joinKey = joinKeyQueryResults.createJoinKey();

        // then
        assertEquals( "test.Author_Publisher.author_first_name\0test.Author_Publisher.author_last_name", joinKey
                .sourceColumn().name() );
        assertEquals( "test.Author.first_name\0test.Author.last_name", joinKey.targetColumn().name() );
    }
}
