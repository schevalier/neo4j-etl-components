package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.TestUtil;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class TableToCsvFieldMapperTest
{

    private final TestUtil testUtil = new TestUtil();

    @Test
    public void shouldCreatePrimaryKeyAndDataMappingsForTable()
    {
        // given
        TableName personTable = new TableName( "test.Person" );

        Table table = Table.builder()
                .name( personTable )
                .addColumn( testUtil.column( personTable, "id", ColumnType.PrimaryKey ) )
                .addColumn( testUtil.column( personTable, "username", ColumnType.Data ) )
                .addColumn( new SimpleColumn( personTable,
                        personTable.fullyQualifiedColumnName( "age" ),
                        "age",
                        ColumnType.Data,
                        MySqlDataType.INT ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
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
                .addColumn( new CompositeKeyColumn(
                        authorTable,
                        Arrays.asList(
                                new SimpleColumn(
                                        authorTable,
                                        authorTable.fullyQualifiedColumnName( "first_name" ),
                                        "first_name",
                                        ColumnType.PrimaryKey,
                                        MySqlDataType.VARCHAR ),
                                new SimpleColumn(
                                        authorTable,
                                        authorTable.fullyQualifiedColumnName( "last_name" ),
                                        "last_name",
                                        ColumnType.PrimaryKey,
                                        MySqlDataType.VARCHAR ) ) ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Author" ) ),
                CsvField.label() ) );
    }

    @Test
    public void shouldNotCreateMappingForForeignKey()
    {
        // given
        TableName personTable = new TableName( "test.Person" );

        Table table = Table.builder()
                .name( personTable )
                .addColumn( testUtil.column( personTable, "id", ColumnType.PrimaryKey ) )
                .addColumn( testUtil.column( personTable, "username", ColumnType.Data ) )
                .addColumn( testUtil.column( personTable, "addressId", ColumnType.ForeignKey ) )
                .build();

        TableToCsvFieldMapper mapper = new TableToCsvFieldMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( table );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.id( new IdSpace( "test.Person" ) ),
                CsvField.data( "username", Neo4jDataType.String ),
                CsvField.label() ) );
    }
}
