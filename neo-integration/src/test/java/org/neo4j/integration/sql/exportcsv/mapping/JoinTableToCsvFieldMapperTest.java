package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

public class JoinTableToCsvFieldMapperTest
{

    @Test
    public void shouldCreateMappingsForJoinTable()
    {
        // given

        TableName joinTableName = new TableName( "test.Student_Course" );
        TableName startTableName = new TableName( "test.Student" );
        TableName endTableName = new TableName( "test.Course" );

        Column startForeignKey = getBuild( joinTableName, "studentId", "studentId", ColumnType.ForeignKey );
        Column startPrimaryKey = getBuild( startTableName, "id", "id", ColumnType.PrimaryKey );

        Column endForeignKey = getBuild( joinTableName, "courseId", "courseId", ColumnType.ForeignKey );
        Column endPrimaryKey = getBuild( endTableName, "id", "id", ColumnType.PrimaryKey );

        JoinTable joinTable = JoinTable.builder()
                .startForeignKey( startForeignKey )
                .connectsToStartTablePrimaryKey( startPrimaryKey )
                .endForeignKey( endForeignKey )
                .connectsToEndTablePrimaryKey( endPrimaryKey )
                .build();

        JoinTableToCsvFieldMapper mapper = new JoinTableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( joinTable );

        // then
        Collection<CsvField> fields = new ArrayList<>( mappings.fields() );
        Collection<String> columns = mappings.columns().stream().map( Column::name ).collect( Collectors.toList() );

        assertEquals( fields, asList(
                CsvField.startId( new IdSpace( "test.Student" ) ),
                CsvField.endId( new IdSpace( "test.Course" ) ),
                CsvField.relationshipType() ) );

        assertEquals( asList( "test.Student_Course.studentId", "test.Student_Course.courseId", "\"STUDENT_COURSE\"" )
                , columns );
    }

    private Column getBuild( TableName tableName, String name, String alias, ColumnType columnType )
    {
        return new Column(
                tableName,
                tableName.fullyQualifiedColumnName( name ),
                alias,
                columnType,
                SqlDataType.KEY_DATA_TYPE );
    }
}
