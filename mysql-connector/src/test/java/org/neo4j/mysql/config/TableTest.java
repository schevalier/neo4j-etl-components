package org.neo4j.mysql.config;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.ingest.config.Data;
import org.neo4j.ingest.config.DataType;
import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.Id;
import org.neo4j.ingest.config.Label;

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
                .addColumn( Column.withName( "id" ).mapsTo( new Field( Id.ID ) ) )
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
                .addColumn( Column.withName( "id" ).mapsTo( new Field( Id.ID ) ) )
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
        Field field1 = new Field( Id.ID );
        Field field2 = new Field( "name", Data.ofType( DataType.String ) );
        Field field3 = new Field( Label.label() );

        Table table = Table.builder()
                .name( "example.Person" )
                .addColumn( Column.withName( "id" ).mapsTo( field1 ) )
                .addColumn( Column.withName( "name" ).mapsTo( field2 ) )
                .addColumn( Column.withName( "age" ).mapsTo( field3 ) )
                .build();

        // when
        Collection<Field> fields = table.fieldMappings();

        // then
        assertEquals( asList( field1, field2, field3 ), fields );
    }
}
