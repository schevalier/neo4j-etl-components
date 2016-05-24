package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

public class JoinTableToCsvFieldMapperTest
{
    private ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldCreateMappingsForJoinTableWithProperties()
    {
        // given

        TableName joinTableName = new TableName( "test.Student_Course" );
        TableName leftTable = new TableName( "test.Student" );
        TableName rightTable = new TableName( "test.Course" );

        Column keyOneSourceColumn = columnUtil.keyColumn( joinTableName, "studentId", ColumnRole.ForeignKey );
        Column keyOneTargetColumn = columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey );

        Column keyTwoSourceColumn = columnUtil.keyColumn( joinTableName, "courseId", ColumnRole.ForeignKey );
        Column keyTwoTargetColumn = columnUtil.keyColumn( rightTable, "id", ColumnRole.PrimaryKey );

        JoinTable joinTable = new JoinTable(
                new Join(
                        new JoinKey( keyOneSourceColumn, keyOneTargetColumn ),
                        new JoinKey( keyTwoSourceColumn, keyTwoTargetColumn )
                ),
                Table.builder()
                        .name( joinTableName )
                        .addColumn( columnUtil.column( joinTableName, "credits", "credits", ColumnRole.Data ) )
                        .build() );

        JoinTableToCsvFieldMapper mapper = new JoinTableToCsvFieldMapper( Formatting.DEFAULT,
                new RelationshipNameResolver( RelationshipNameFrom.TABLE_NAME ) );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( joinTable );

        // then
        Collection<CsvField> fields = new ArrayList<>( mappings.fields() );
        Collection<String> columns = mappings.columns().stream().map( Column::name ).collect( Collectors.toList() );

        assertEquals( fields, asList(
                CsvField.startId( new IdSpace( "test.Student" ) ),
                CsvField.endId( new IdSpace( "test.Course" ) ),
                CsvField.relationshipType(),
                CsvField.data( "credits", Neo4jDataType.String ) ) );

        assertEquals(
                asList( "test.Student_Course.studentId", "test.Student_Course.courseId", "\"STUDENT_COURSE\"",
                        "test.Student_Course.credits" )
                , columns );
    }

    @Test
    public void shouldCreateMappingsForJoinTable()
    {
        // given

        TableName joinTableName = new TableName( "test.Student_Course" );
        TableName leftTable = new TableName( "test.Student" );
        TableName rightTable = new TableName( "test.Course" );

        Column keyOneSourceColumn = columnUtil.keyColumn( joinTableName, "studentId", ColumnRole.ForeignKey );
        Column keyOneTargetColumn = columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey );

        Column keyTwoSourceColumn = columnUtil.keyColumn( joinTableName, "courseId", ColumnRole.ForeignKey );
        Column keyTwoTargetColumn = columnUtil.keyColumn( rightTable, "id", ColumnRole.PrimaryKey );

        JoinTable joinTable = new JoinTable(
                new Join(
                        new JoinKey( keyOneSourceColumn, keyOneTargetColumn ),
                        new JoinKey( keyTwoSourceColumn, keyTwoTargetColumn )
                ),
                Table.builder().name( joinTableName ).build() );

        JoinTableToCsvFieldMapper mapper = new JoinTableToCsvFieldMapper(
                Formatting.DEFAULT,
                new RelationshipNameResolver( RelationshipNameFrom.COLUMN_NAME ) );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( joinTable );

        // then
        Collection<CsvField> fields = new ArrayList<>( mappings.fields() );
        Collection<String> columns = mappings.columns().stream().map( Column::name ).collect( Collectors.toList() );

        assertEquals( fields, asList(
                CsvField.startId( new IdSpace( "test.Student" ) ),
                CsvField.endId( new IdSpace( "test.Course" ) ),
                CsvField.relationshipType() ) );

        assertEquals( asList( "test.Student_Course.studentId", "test.Student_Course.courseId", "\"COURSE_ID\"" )
                , columns );
    }
}
