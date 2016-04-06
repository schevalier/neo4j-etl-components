package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static java.util.Arrays.asList;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinTableMetadataProducerTest
{

    private ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldReturnJoinTableMetadata() throws Exception
    {
        // given
        QueryResults columnProjectionResults = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_KEY" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( stubJoinResults() ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor(
                        new JoinTableInfo(
                                new TableName( "test.Student_Course" ),
                                new TableNamePair(
                                        new TableName( "test.Student" ),
                                        new TableName( "test.Course" ) ) ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        assertJoinTableKeyMappings( joinTable );

        assertTrue( joinTable.columns().isEmpty() );

        assertFalse( iterator.hasNext() );
    }

    @Test
    public void shouldReturnJoinTableMetadataForJoinThroughCompositeKeys() throws Exception
    {
        // given
        QueryResults columnProjectionResults = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_KEY" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        QueryResults joinResults = StubQueryResults.builder()
                .columns( "SOURCE_TABLE_SCHEMA",
                        "SOURCE_TABLE_NAME",
                        "SOURCE_COLUMN_NAME",
                        "SOURCE_COLUMN_TYPE",
                        "TARGET_TABLE_SCHEMA",
                        "TARGET_TABLE_NAME",
                        "TARGET_COLUMN_NAME",
                        "TARGET_COLUMN_TYPE" )
                .addRow( "test", "Author_Publisher", "publisherId", "ForeignKey", "test", "Publisher", "id",
                        "PrimaryKey" )
                .addRow( "test", "Author_Publisher", "author_first_name", "ForeignKey", "test", "Author", "first_name",
                        "PrimaryKey" )
                .addRow( "test", "Author_Publisher", "author_last_name", "ForeignKey", "test", "Author", "last_name",
                        "PrimaryKey" )
                .build();
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( joinResults ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        TableName author = new TableName( "test.Author" );
        TableName publisher = new TableName( "test.Publisher" );

        // then
        TableName authorPublisher = new TableName( "test.Author_Publisher" );
        ArrayList<JoinTable> joinTables = new ArrayList<>( metadataProducer.createMetadataFor(
                new JoinTableInfo( authorPublisher, new TableNamePair( author, publisher ) ) ) );

        JoinTable joinTable = joinTables.get( 0 );

        Join join = joinTable.join();

        assertEquals( columnUtil.compositeKeyColumn( authorPublisher,
                asList( "author_first_name", "author_last_name" ), ColumnRole.ForeignKey ), join.keyOneSourceColumn() );

        assertEquals(
                columnUtil.keyColumn( authorPublisher, "publisherId", ColumnRole.ForeignKey ),
                join.keyTwoSourceColumn() );

        assertEquals(
                columnUtil.keyColumn( publisher, "id", ColumnRole.PrimaryKey ),
                join.keyTwoTargetColumn() );

        assertEquals(
                columnUtil.compositeKeyColumn( author, asList( "first_name", "last_name" ), ColumnRole.PrimaryKey ),
                join.keyOneTargetColumn() );

        assertTrue( joinTables.size() == 1 );
    }

    @Test
    public void shouldReturnJoinTableMetadataForJoinsWithProperties() throws Exception
    {
        // given
        QueryResults columnProjectionResults = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_TYPE" )
                .addRow( "credits", "text", "Data" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( stubJoinResults() ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        TableName studentCourse = new TableName( "test.Student_Course" );
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor(
                        new JoinTableInfo(
                                studentCourse,
                                new TableNamePair(
                                        new TableName( "test.Student" ),
                                        new TableName( "test.Course" ) ) ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        assertJoinTableKeyMappings( joinTable );

        assertThat( joinTable.columns(), contains(
                columnUtil.column( studentCourse, "credits", ColumnRole.Data ) ) );

        assertFalse( iterator.hasNext() );
    }

    @Test
    public void shouldIgnoreKeysForJoinsWithProperties() throws Exception
    {
        // given
        QueryResults columnProjectionResults = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_TYPE" )
                .addRow( "credits", "text", "Data" )
                .addRow( "studentId", "int", "PrimaryKey" )
                .addRow( "addressId", "int", "ForeignKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( stubJoinResults() ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        TableName studentCourse = new TableName( "test.Student_Course" );
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor(
                        new JoinTableInfo(
                                studentCourse,
                                new TableNamePair(
                                        new TableName( "test.Student" ),
                                        new TableName( "test.Course" ) ) ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        assertJoinTableKeyMappings( joinTable );

        assertThat( joinTable.columns(), contains(
                columnUtil.column( studentCourse, "credits", ColumnRole.Data ) ) );

        assertFalse( iterator.hasNext() );
    }

    private void assertJoinTableKeyMappings( JoinTable joinTable )
    {
        TableName studentCourse = joinTable.joinTableName();
        Column expectedStudentId = columnUtil.keyColumn( studentCourse, "studentId", ColumnRole.ForeignKey );
        Column expectedCourseId = columnUtil.keyColumn( studentCourse, "courseId", ColumnRole.ForeignKey );

        assertEquals( expectedStudentId, joinTable.join().keyTwoSourceColumn() );

        assertEquals( columnUtil.keyColumn( new TableName( "test.Student" ), "id", ColumnRole.PrimaryKey ),
                joinTable.join().keyTwoTargetColumn() );

        assertEquals( expectedCourseId, joinTable.join().keyOneSourceColumn() );

        assertEquals( columnUtil.keyColumn( new TableName( "test.Course" ), "id", ColumnRole.PrimaryKey ),
                joinTable.join().keyOneTargetColumn() );
    }

    private QueryResults stubJoinResults()
    {
        return StubQueryResults.builder()
                .columns( "SOURCE_TABLE_SCHEMA",
                        "SOURCE_TABLE_NAME",
                        "SOURCE_COLUMN_NAME",
                        "SOURCE_COLUMN_TYPE",
                        "TARGET_TABLE_SCHEMA",
                        "TARGET_TABLE_NAME",
                        "TARGET_COLUMN_NAME",
                        "TARGET_COLUMN_TYPE" )
                .addRow( "test", "Student_Course", "studentId", "ForeignKey", "test", "Student", "id", "PrimaryKey" )
                .addRow( "test", "Student_Course", "courseId", "ForeignKey", "test", "Course", "id", "PrimaryKey" )
                .build();
    }
}
