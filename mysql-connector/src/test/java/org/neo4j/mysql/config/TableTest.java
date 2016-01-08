package org.neo4j.mysql.config;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.ingest.config.DataType;
import org.neo4j.ingest.config.Field;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

public class TableTest
{
    @Test
    public void shouldReturnSimpleNameFromQualifiedName()
    {
        // given
        Table table = Table.builder()
                .name( "example.Person" )
                .addColumn( Column.builder()
                        .name( "id" )
                        .mapsTo( Field.id() )
                        .build() )
                .build();

        // when
        String simpleName = table.simpleName();

        // then
        assertEquals( "Person", simpleName );
    }

    @Test
    public void shouldReturnSimpleNameFromSimpleName()
    {
        // given
        Table table = Table.builder()
                .name( "Person" )
                .addColumn( Column.builder()
                        .name( "id" )
                        .mapsTo( Field.id() )
                        .build() )
                .build();

        // when
        String simpleName = table.simpleName();

        // then
        assertEquals( "Person", simpleName );
    }

    @Test
    public void shouldReturnFieldMappingsForColumns()
    {
        // given
        Field field1 = Field.id();
        Field field2 = Field.data( "name", DataType.String );
        Field field3 = Field.label();

        Table table = Table.builder()
                .name( "example.Person" )
                .addColumn( Column.builder().name( "id" ).mapsTo( field1 ).build() )
                .addColumn( Column.builder().name( "name" ).mapsTo( field2 ).build() )
                .addColumn( Column.builder().name( "age" ).mapsTo( field3 ).build() )
                .build();

        // when
        Collection<Field> fields = table.fieldMappings();

        // then
        assertEquals( asList( field1, field2, field3 ), fields );
    }
}
