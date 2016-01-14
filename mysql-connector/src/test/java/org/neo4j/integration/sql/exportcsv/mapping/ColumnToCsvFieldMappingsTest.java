package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

public class ColumnToCsvFieldMappingsTest
{
    @Test
    public void shouldReturnCollectionOfFullyQualifiedColumnNames()
    {
        // given
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "id" )
                .type( ColumnType.PrimaryKey ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "username" )
                .type( ColumnType.Data ).build();

        Column column3 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "age" )
                .type( ColumnType.Data ).build();


        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username" ) )
                .add( column3, CsvField.data( "age" ) )
                .build();


        // when
        Collection<String> columns = mappings.columns();

        // then
        assertEquals( asList( "test.Person.id", "test.Person.username", "test.Person.age" ), columns );
    }

    @Test
    public void shouldReturnCollectionOfFullyQualifiedTableNames()
    {
        // given
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "id" )
                .type( ColumnType.PrimaryKey ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "username" )
                .type( ColumnType.Data ).build();

        Column column3 = Column.builder()
                .table( new TableName( "test.Address" ) )
                .name( "id" )
                .type( ColumnType.PrimaryKey ).build();


        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username" ) )
                .add( column3, CsvField.data( "age" ) )
                .build();

        // when
        Collection<String> tableNames = mappings.tableNames();

        // then
        assertEquals( new LinkedHashSet<>( asList( "test.Person", "test.Address" ) ), tableNames );
    }
}
