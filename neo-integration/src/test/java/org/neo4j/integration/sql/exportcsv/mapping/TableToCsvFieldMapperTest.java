package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class TableToCsvFieldMapperTest
{

    private final ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldCreatePrimaryKeyAndDataMappingsForTable()
    {
        // given
        TableName personTable = new TableName( "test.Person" );

        Table table = Table.builder()
                .name( personTable )
                .addColumn( columnUtil.column( personTable, "id", ColumnRole.PrimaryKey ) )
                .addColumn( columnUtil.column( personTable, "username", ColumnRole.Data ) )
                .addColumn( new SimpleColumn( personTable, "age", EnumSet.of( ColumnRole.Data ), SqlDataType.INT ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
                CsvField.data( "id", Neo4jDataType.String ),
                CsvField.data( "username", Neo4jDataType.String ),
                CsvField.data( "age", Neo4jDataType.Int ),
                CsvField.label() ) );
    }

    @Test
    public void shouldCreateCompositeKeyMappingsForTable()
    {
        // given
        TableName authorTable = new TableName( "test.Author" );

        Table table = Table.builder()
                .name( authorTable )
                .addColumn( new ColumnUtil().compositeKeyColumn( authorTable, asList( "first_name", "last_name" ),
                        ColumnRole.PrimaryKey ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Author" ) ),
                CsvField.data( "first_name", Neo4jDataType.String ),
                CsvField.data( "last_name", Neo4jDataType.String ),
                CsvField.label() ) );
    }

    @Test
    public void shouldNotCreateMappingForForeignKey()
    {
        // given
        TableName personTable = new TableName( "test.Person" );

        Table table = Table.builder()
                .name( personTable )
                .addColumn( columnUtil.column( personTable, "id", ColumnRole.PrimaryKey ) )
                .addColumn( columnUtil.column( personTable, "username", ColumnRole.Data ) )
                .addColumn( columnUtil.column( personTable, "addressId", ColumnRole.ForeignKey ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
                CsvField.data( "id", Neo4jDataType.String ),
                CsvField.data( "username", Neo4jDataType.String ),
                CsvField.label() ) );
    }
}
