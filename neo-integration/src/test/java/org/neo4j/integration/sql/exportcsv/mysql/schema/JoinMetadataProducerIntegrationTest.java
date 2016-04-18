package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class JoinMetadataProducerIntegrationTest
{
    private final ColumnUtil columnUtil = new ColumnUtil();
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void shouldReturnJoinMetadataRelationshipCompositeKey() throws Exception
    {
        // given

        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( "localhost" )
                .port( 3306 )
                .database( "javabase" )
                .username( "neo" )
                .password( "neo" )
                .build();

        DatabaseClient databaseClient = new DatabaseClient( connectionConfig );

        JoinMetadataProducer getJoinMetadata = new JoinMetadataProducer( databaseClient );

        // when
        TableName book = new TableName( "javabase.Book" );
        TableName author = new TableName( "javabase.Author" );
        Collection<Join> joinCollection = getJoinMetadata.createMetadataFor( new TableNamePair( book, author ) );

        // then
        ArrayList<Join> joins = new ArrayList<>( joinCollection );
        Join writtenBy = joins.get( 0 );

        assertEquals( columnUtil.keyColumn( book, "id", ColumnRole.PrimaryKey ), writtenBy.keyOneSourceColumn() );

        assertEquals(
                columnUtil.compositeKeyColumn(
                        book, asList( "author_first_name", "author_last_name" ), ColumnRole.ForeignKey ),
                writtenBy.keyTwoSourceColumn() );

        assertEquals(
                columnUtil.compositeKeyColumn( author, asList( "first_name", "last_name" ), ColumnRole.PrimaryKey ),
                writtenBy.keyTwoTargetColumn() );

        assertEquals( author, writtenBy.keyTwoTargetColumn().table() );

        assertTrue( joins.size() == 1 );
    }

    @Test
    public void shouldReturnJoinMetadataRelationshipCompositeKeyHavingPrimaryKeys() throws Exception
    {
        // given

        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( "localhost" )
                .port( 3306 )
                .database( "ngsdb" )
                .username( "neo" )
                .password( "neo" )
                .build();

        DatabaseClient databaseClient = new DatabaseClient( connectionConfig );

        JoinMetadataProducer getJoinMetadata = new JoinMetadataProducer( databaseClient );

        // when
        TableName pendingData = new TableName( "ngsdb.dbmirror_pendingdata" );
        TableName pending = new TableName( "ngsbd.dbmirror_pending" );
        Collection<Join> joinCollection = getJoinMetadata.createMetadataFor(
                new TableNamePair( pendingData, pending ) );

        // then
        ArrayList<Join> joins = new ArrayList<>( joinCollection );
        Join join = joins.get( 0 );

        assertEquals( columnUtil.compositeKeyColumn( pendingData, asList( "IdKey", "SeqId" ), ColumnRole.PrimaryKey ),
                join.keyOneSourceColumn() );

        assertEquals(
                columnUtil.compositeKeyColumn( pendingData, asList( "IdKey", "SeqId" ), ColumnRole.PrimaryKey ),
                join.keyOneTargetColumn() );

        assertEquals(
                columnUtil.keyColumn( pendingData, "IdKey", ColumnRole.PrimaryKey ),
                join.keyTwoSourceColumn() );

        assertEquals( pending, join.keyTwoTargetColumn().table() );

        assertTrue( joins.size() == 1 );
    }
}
