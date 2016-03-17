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
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinMetadataProducerTest
{
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
                SqlDataType.KEY_DATA_TYPE ), livesIn.leftSource() );

        assertEquals( new SimpleColumn(
                new TableName( "test.Person" ),
                "test.Person.addressId",
                "addressId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE ), livesIn.rightSource() );

        assertEquals( new TableName( "test.Address" ), livesIn.rightTarget().table() );

        Join ownedBy = joins.get( 0 );

        assertEquals( new SimpleColumn(
                new TableName( "test.Address" ),
                "test.Address.id",
                "id",
                ColumnType.PrimaryKey,
                SqlDataType.KEY_DATA_TYPE ), ownedBy.leftSource() );

        assertEquals( new SimpleColumn(
                new TableName( "test.Address" ),
                "test.Address.ownerId",
                "ownerId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE ), ownedBy.rightSource() );

        assertEquals( new TableName( "test.Person" ), ownedBy.rightTarget().table() );

        assertTrue( joins.size() == 2 );
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
