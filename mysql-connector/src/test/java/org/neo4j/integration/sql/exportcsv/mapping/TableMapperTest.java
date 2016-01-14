package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.DataType;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Table;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class TableMapperTest
{
    @Test
    public void shouldCreateMappingsForTable()
    {
        // given
        Table table = Table.builder()
                .name( "test.Person" )
                .addColumn( "id", ColumnType.PrimaryKey )
                .addColumn( "username", ColumnType.Data )
                .build();

        TableMapper mapper = new TableMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
                CsvField.data( "username", DataType.String ),
                CsvField.label() ) );
    }

    @Test
    public void shouldNotCreateMappingForForeignKey()
    {
        // given
        Table table = Table.builder()
                .name( "test.Person" )
                .addColumn( "id", ColumnType.PrimaryKey )
                .addColumn( "username", ColumnType.Data )
                .addColumn( "addressId", ColumnType.ForeignKey )
                .build();

        TableMapper mapper = new TableMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
                CsvField.data( "username", DataType.String ),
                CsvField.label() ) );
    }
}
