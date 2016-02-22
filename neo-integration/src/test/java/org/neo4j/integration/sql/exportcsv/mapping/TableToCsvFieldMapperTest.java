package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.DataType;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class TableToCsvFieldMapperTest
{
    @Test
    public void shouldCreateMappingsForTable()
    {
        // given
        TableName personTable = new TableName( "test.Person" );

        Table table = Table.builder()
                .name( personTable )
                .addColumn( column( personTable, "id", ColumnType.PrimaryKey ) )
                .addColumn( column( personTable, "username", ColumnType.Data ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

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
        TableName personTable = new TableName( "test.Person" );

        Table table = Table.builder()
                .name( personTable )
                .addColumn( column( personTable, "id", ColumnType.PrimaryKey ) )
                .addColumn( column( personTable, "username", ColumnType.Data ) )
                .addColumn( column( personTable, "addressId", ColumnType.ForeignKey ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
                CsvField.data( "username", DataType.String ),
                CsvField.label() ) );
    }

    private Column column( TableName table, String name, ColumnType type )
    {
        return Column.builder()
                .table( table )
                .name( table.fullyQualifiedColumnName( name ) )
                .alias( name )
                .type( type )
                .build();
    }
}
