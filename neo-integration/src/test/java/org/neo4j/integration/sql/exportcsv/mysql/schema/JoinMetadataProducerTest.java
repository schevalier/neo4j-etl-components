package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinMetadataProducerTest
{
    private final ColumnUtil columnUtil = new ColumnUtil();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnJoinMetadataForTwoWayRelationships() throws Exception
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
                .addRow( "test", "Person", "id", "PrimaryKey", "test", "Person", "id", "PrimaryKey" )
                .addRow( "test", "Person", "addressId", "ForeignKey", "test", "Address", "id", "PrimaryKey" )
                .addRow( "test", "Address", "id", "PrimaryKey", "test", "Address", "id", "PrimaryKey" )
                .addRow( "test", "Address", "ownerId", "ForeignKey", "test", "Person", "id", "PrimaryKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        JoinMetadataProducer getJoinMetadata = new JoinMetadataProducer( databaseClient );

        // when
        Collection<Join> joinCollection = getJoinMetadata
                .createMetadataFor( new TableNamePair(
                        new TableName( "test.Person" ),
                        new TableName( "test.Address" ) ) );

        // then
        ArrayList<Join> joins = new ArrayList<>( joinCollection );
        Join livesIn = joins.get( 1 );

        assertEquals( new SimpleColumn(
                new TableName( "test.Person" ),
                "test.Person.id",
                "id",
                ColumnType.PrimaryKey,
                SqlDataType.KEY_DATA_TYPE ), livesIn.keyOneSourceColumn() );

        assertEquals( new SimpleColumn(
                new TableName( "test.Person" ),
                "test.Person.addressId",
                "addressId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE ), livesIn.keyTwoSourceColumn() );

        assertEquals( new TableName( "test.Address" ), livesIn.keyTwoTargetColumn().table() );

        Join ownedBy = joins.get( 0 );

        assertEquals( new SimpleColumn(
                new TableName( "test.Address" ),
                "test.Address.id",
                "id",
                ColumnType.PrimaryKey,
                SqlDataType.KEY_DATA_TYPE ), ownedBy.keyOneSourceColumn() );

        assertEquals( new SimpleColumn(
                new TableName( "test.Address" ),
                "test.Address.ownerId",
                "ownerId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE ), ownedBy.keyTwoSourceColumn() );

        assertEquals( new TableName( "test.Person" ), ownedBy.keyTwoTargetColumn().table() );

        assertTrue( joins.size() == 2 );
    }

    @Test
    public void shouldReturnJoinMetadataRelationshipCompositeKey() throws Exception
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
                .addRow( "test", "Book", "author_first_name", "ForeignKey", "test", "Author", "first_name",
                        "PrimaryKey" )
                .addRow( "test", "Book", "author_last_name", "ForeignKey", "test", "Author", "last_name", "PrimaryKey" )
                .addRow( "test", "Book", "id", "PrimaryKey", "test", "Book", "id", "PrimaryKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        JoinMetadataProducer getJoinMetadata = new JoinMetadataProducer( databaseClient );

        // when
        TableName book = new TableName( "test.Book" );
        TableName author = new TableName( "test.Author" );
        Collection<Join> joinCollection = getJoinMetadata.createMetadataFor(
                new TableNamePair( book, author ) );

        // then
        ArrayList<Join> joins = new ArrayList<>( joinCollection );
        Join writtenBy = joins.get( 0 );

        assertEquals( new SimpleColumn(
                book,
                "test.Book.id",
                "id",
                ColumnType.PrimaryKey,
                SqlDataType.KEY_DATA_TYPE ), writtenBy.keyOneSourceColumn() );

        assertEquals( new CompositeKeyColumn( new TableName( "test.Book" ),
                asList( new SimpleColumn(
                                new TableName( "test.Book" ),
                                "test.Book.author_first_name",
                                "author_first_name",
                                ColumnType.ForeignKey,
                                SqlDataType.KEY_DATA_TYPE ),
                        new SimpleColumn(
                                new TableName( "test.Book" ),
                                "test.Book.author_last_name",
                                "author_last_name",
                                ColumnType.ForeignKey,
                                SqlDataType.KEY_DATA_TYPE ) )
        ), writtenBy.keyTwoSourceColumn() );

        assertEquals( columnUtil.compositeColumn( author, asList( "first_name", "last_name" ) ),
                writtenBy.keyTwoTargetColumn() );

        assertEquals( author, writtenBy.keyTwoTargetColumn().table() );

        assertTrue( joins.size() == 1 );
    }

    @Test
    public void shouldThrowExceptionIfJoinDoesNotExistBetweenSuppliedTables() throws Exception
    {
        thrown.expect( IllegalStateException.class );
        thrown.expectMessage( "No join exists between 'test.Person' and 'test.Course'" );

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
                .addRow( "test", "Person", "id", "PrimaryKey", "test", "Person", "id", "PrimaryKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        JoinMetadataProducer getJoinMetadata = new JoinMetadataProducer( databaseClient );


        // when
        getJoinMetadata.createMetadataFor(
                new TableNamePair( new TableName( "test.Person" ), new TableName( "test.Course" ) ) );

    }
}
