package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

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
    @Test
    public void shouldReturnJoinTableMetadata() throws Exception
    {
        // given
        QueryResults joinResults = StubQueryResults.builder()
                .columns( "TABLE_SCHEMA",
                        "TABLE_NAME",
                        "FOREIGN_KEY",
                        "REFERENCED_PRIMARY_KEY",
                        "REFERENCED_TABLE_SCHEMA",
                        "REFERENCED_TABLE_NAME" )
                .addRow( "test", "Student_Course", "studentId", "id", "test", "Student" )
                .addRow( "test", "Student_Course", "courseId", "id", "test", "Course" )
                .build();
        QueryResults columnProjectionResults = mock( QueryResults.class );
        when( columnProjectionResults.next() ).thenReturn( false );

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( joinResults ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor( new JoinTableInfo( new TableName( "test.Student_Course" ), new TableNamePair(
                        new TableName( "test.Student" ),
                        new TableName( "test.Course" ) ) ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        assertEquals( new Column(
                        new TableName( "test.Student_Course" ),
                        "test.Student_Course.studentId",
                        "studentId",
                        ColumnType.ForeignKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.startForeignKey() );

        assertEquals( new Column(
                        new TableName( "test.Student" ),
                        "test.Student.id",
                        "id",
                        ColumnType.PrimaryKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.startPrimaryKey() );

        assertEquals( new Column(
                        new TableName( "test.Student_Course" ),
                        "test.Student_Course.courseId",
                        "courseId",
                        ColumnType.ForeignKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.endForeignKey() );

        assertEquals( new Column(
                        new TableName( "test.Course" ),
                        "test.Course.id", "id",
                        ColumnType.PrimaryKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.endPrimaryKey() );

        assertTrue( joinTable.columns().isEmpty() );

        assertFalse( iterator.hasNext() );
    }

    @Test
    public void shouldReturnJoinTableMetadataForJoinsWithProperties() throws Exception
    {
        // given
        QueryResults joinResults = StubQueryResults.builder()
                .columns( "TABLE_SCHEMA",
                        "TABLE_NAME",
                        "FOREIGN_KEY",
                        "REFERENCED_PRIMARY_KEY",
                        "REFERENCED_TABLE_SCHEMA",
                        "REFERENCED_TABLE_NAME" )
                .addRow( "test", "Student_Course", "studentId", "id", "test", "Student" )
                .addRow( "test", "Student_Course", "courseId", "id", "test", "Course" )
                .build();
        QueryResults columnProjectionResults = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_KEY" )
                .addRow( "credits", "text", "" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) )
                .thenReturn( AwaitHandle.forReturnValue( joinResults ) )
                .thenReturn( AwaitHandle.forReturnValue( columnProjectionResults ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        TableName studentCourse = new TableName( "test.Student_Course" );
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor(
                        new JoinTableInfo( studentCourse,
                                new TableNamePair( new TableName( "test.Student" ), new TableName( "test.Course" ) )
                        ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        assertEquals( new Column(
                        studentCourse,
                        "test.Student_Course.studentId",
                        "studentId",
                        ColumnType.ForeignKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.startForeignKey() );

        assertEquals( new Column(
                        new TableName( "test.Student" ),
                        "test.Student.id",
                        "id",
                        ColumnType.PrimaryKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.startPrimaryKey() );

        assertEquals( new Column(
                        studentCourse,
                        "test.Student_Course.courseId",
                        "courseId",
                        ColumnType.ForeignKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.endForeignKey() );

        assertEquals( new Column(
                        new TableName( "test.Course" ),
                        "test.Course.id", "id",
                        ColumnType.PrimaryKey,
                        SqlDataType.KEY_DATA_TYPE ),
                joinTable.endPrimaryKey() );

        assertThat( joinTable.columns(), contains(
                new Column(
                        studentCourse,
                        studentCourse.fullyQualifiedColumnName( "credits" ),
                        "credits",
                        ColumnType.Data,
                        MySqlDataType.TEXT ) ) );

        assertFalse( iterator.hasNext() );
    }
}
