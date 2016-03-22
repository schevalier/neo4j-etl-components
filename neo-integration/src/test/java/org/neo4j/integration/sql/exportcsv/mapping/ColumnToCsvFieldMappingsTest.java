package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.TestUtil;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class ColumnToCsvFieldMappingsTest
{

    private TestUtil testUtil = new TestUtil();

    @Test
    public void shouldReturnCollectionOfCsvFields()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column1 = testUtil.column( personTable, "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = testUtil.column( personTable, "test.Person.username", "username", ColumnType.Data );

        Column column3 = testUtil.column( personTable, "test.Person.age", "age", ColumnType.Data );

        CsvField idField = CsvField.id();
        CsvField usernameField = CsvField.data( "username", Neo4jDataType.String );
        CsvField ageField = CsvField.data( "age", Neo4jDataType.String );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, idField )
                .add( column2, usernameField )
                .add( column3, ageField )
                .build();

        // when
        Collection<CsvField> fields = mappings.fields();

        // then
        assertThat( fields, hasItems( idField, usernameField, ageField ) );
    }

    @Test
    public void shouldReturnCollectionOfFullyQualifiedColumnNames()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column1 = testUtil.column( personTable, "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = testUtil.column( personTable, "test.Person.username", "username", ColumnType.Data );

        Column column3 = testUtil.column( personTable, "test.Person.age", "age", ColumnType.Data );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username", Neo4jDataType.String ) )
                .add( column3, CsvField.data( "age", Neo4jDataType.String ) )
                .build();

        // when
        Collection<Column> columns = mappings.columns();

        // then
        assertThat( columns, hasItems( column1, column2, column3 ) );
    }

    @Test
    public void shouldReturnCollectionOfAliasedColumnNames()
    {
        // given
        TableName authorTable = new TableName( "test.Author" );
        Column column1 = new CompositeKeyColumn( authorTable,
                asList( new SimpleColumn(
                                new TableName( "test.Author" ),
                                "test.Author.first_name",
                                "first_name",
                                ColumnType.PrimaryKey,
                                SqlDataType.KEY_DATA_TYPE ),
                        new SimpleColumn(
                                new TableName( "test.Author" ),
                                "test.Author.last_name",
                                "last_name",
                                ColumnType.PrimaryKey,
                                SqlDataType.KEY_DATA_TYPE ) ) );


        Column column2 = testUtil.column( authorTable, "test.Author.age", "age", ColumnType.Data );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "age", Neo4jDataType.String ) )
                .build();

        // when
        Collection<String> aliasedColumns = mappings.aliasedColumns();

        // then
        assertThat( aliasedColumns,
                hasItems(
                        "test.Author.first_name AS first_name, test.Author.last_name AS last_name",
                        "test.Author.age AS age" ) );
    }

    @Test
    public void shouldReturnCollectionOfFullyQualifiedTableNames()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column1 = testUtil.column( personTable, "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = testUtil.column( personTable, "test.Person.username", "username", ColumnType.Data );
        Column column3 = testUtil.column( new TableName( "test.Address" ), "test.Address.postcode", "postcode",
                ColumnType.PrimaryKey );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username", Neo4jDataType.String ) )
                .add( column3, CsvField.data( "id", Neo4jDataType.String ) )
                .build();

        // when
        Collection<String> tableNames = mappings.tableNames();

        // then
        assertThat( tableNames, hasItems( "test.Person", "test.Address" ) );
    }
}
