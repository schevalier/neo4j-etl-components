package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class ColumnToCsvFieldMappingsTest
{
    @Test
    public void shouldReturnCollectionOfFullyQualifiedColumnNames()
    {
        // given
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.id" )
                .alias( "id" )
                .type( ColumnType.PrimaryKey ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.username" )
                .alias( "username" )
                .type( ColumnType.Data ).build();

        Column column3 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.age" )
                .alias( "age" )
                .type( ColumnType.Data ).build();

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username" ) )
                .add( column3, CsvField.data( "age" ) )
                .build();

        // when
        Collection<Column> columns = mappings.columns();

        // then
        assertThat( columns, hasItems( column1, column2, column3 ) );
    }

    @Test
    public void shouldReturnCollectionOfFullyQualifiedTableNames()
    {
        // given
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.id" )
                .alias( "id" )
                .type( ColumnType.PrimaryKey ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.username" )
                .alias( "username" )
                .type( ColumnType.Data ).build();

        Column column3 = Column.builder()
                .table( new TableName( "test.Address" ) )
                .name( "test.Address.id" )
                .alias( "id" )
                .type( ColumnType.PrimaryKey ).build();


        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username" ) )
                .add( column3, CsvField.data( "age" ) )
                .build();

        // when
        Collection<String> tableNames = mappings.tableNames();

        // then
        assertThat( tableNames, hasItems( "test.Person", "test.Address" ) );
    }
}
