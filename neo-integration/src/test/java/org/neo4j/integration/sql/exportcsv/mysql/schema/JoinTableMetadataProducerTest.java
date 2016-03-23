package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
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
    @Ignore
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
                .addRow( "test", "Author_Publisher", "author_first_name", "ForeignKey", "test", "Author",
                        "first_name", "PrimaryKey" )
                .addRow( "test", "Author_Publisher", "publisherId", "ForeignKey", "test", "Publisher", "id",
                        "PrimaryKey" )
                .addRow( "test", "Author_Publisher", "author_last_name", "ForeignKey", "test", "Author", "last_name",
                        "PrimaryKey" )
                .build();
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( joinResults ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor(
                        new JoinTableInfo(
                                new TableName( "test.Author_Publisher" ),
                                new TableNamePair(
                                        new TableName( "test.Author" ),
                                        new TableName( "test.Publisher" ) ) ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        TableName authorPublisher = joinTable.joinTableName();
        Column expectedAuthorId = columnUtil.
                compositeForeignKeyColumn( authorPublisher, asList( "author_first_name", "author_last_name" ) );

        Column expectedPublisherId = new SimpleColumn(
                authorPublisher,
                authorPublisher.fullyQualifiedColumnName( "publisherId" ),
                "publisherId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE );

        assertEquals( expectedAuthorId, joinTable.join().keyTwoSourceColumn() );

        assertEquals(
                columnUtil.keyColumn( new TableName( "test.Student" ), "id", ColumnType.PrimaryKey ),
                joinTable.join().keyTwoTargetColumn() );

        assertEquals( expectedPublisherId, joinTable.join().keyOneSourceColumn() );

        assertEquals( columnUtil.keyColumn( new TableName( "test.Course" ), "id", ColumnType.PrimaryKey ),
                joinTable.join().keyOneTargetColumn() );

        assertTrue( joinTable.columns().isEmpty() );

        assertFalse( iterator.hasNext() );
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
                columnUtil.column( studentCourse, "credits", ColumnType.Data ) ) );

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
                columnUtil.column( studentCourse, "credits", ColumnType.Data ) ) );

        assertFalse( iterator.hasNext() );
    }

    private void assertJoinTableKeyMappings( JoinTable joinTable )
    {
        TableName studentCourse = joinTable.joinTableName();
        Column expectedStudentId = new SimpleColumn(
                studentCourse,
                studentCourse.fullyQualifiedColumnName( "studentId" ),
                "studentId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE );
        Column expectedCourseId = new SimpleColumn(
                studentCourse,
                studentCourse.fullyQualifiedColumnName( "courseId" ),
                "courseId",
                ColumnType.ForeignKey,
                SqlDataType.KEY_DATA_TYPE );

        assertEquals( expectedStudentId, joinTable.join().keyTwoSourceColumn() );

        assertEquals( columnUtil.keyColumn( new TableName( "test.Student" ), "id", ColumnType.PrimaryKey ),
                joinTable.join().keyTwoTargetColumn() );

        assertEquals( expectedCourseId, joinTable.join().keyOneSourceColumn() );

        assertEquals( columnUtil.keyColumn( new TableName( "test.Course" ), "id", ColumnType.PrimaryKey ),
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
