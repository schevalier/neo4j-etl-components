package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class ColumnToCsvFieldMappingsTest
{
    @Test
    public void shouldReturnCollectionOfCsvFields()
    {
        // given
        Column column1 = buildColumn( new TableName( "test.Person" ), "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = buildColumn( new TableName( "test.Person" ), "test.Person.username", "username", ColumnType
                .Data );

        Column column3 = buildColumn( new TableName( "test.Person" ), "test.Person.age", "age", ColumnType.Data );

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
        Column column1 = buildColumn( new TableName( "test.Person" ), "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = buildColumn( new TableName( "test.Person" ), "test.Person.username", "username", ColumnType
                .Data );

        Column column3 = buildColumn( new TableName( "test.Person" ), "test.Person.age", "age", ColumnType.Data );

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
        Column column1 = buildColumn( new TableName( "test.Person" ), "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = buildColumn( new TableName( "test.Person" ), "test.Person.username", "username", ColumnType
                .Data );
        ;

        Column column3 = buildColumn( new TableName( "test.Person" ), "test.Person.age", "age", ColumnType.Data );
        ;

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username", Neo4jDataType.String ) )
                .add( column3, CsvField.data( "age", Neo4jDataType.String ) )
                .build();

        // when
        Collection<String> aliasedColumns = mappings.aliasedColumns();

        // then
        assertThat( aliasedColumns,
                hasItems( "test.Person.id AS id", "test.Person.username AS username", "test.Person.age AS age" ) );
    }

    @Test
    public void shouldReturnCollectionOfFullyQualifiedTableNames()
    {
        // given
        Column column1 = buildColumn(
                new TableName( "test.Person" ),
                "test.Person.id",
                "id",
                ColumnType.PrimaryKey );

        Column column2 = buildColumn(
                new TableName( "test.Person" ),
                "test.Person.username",
                "username",
                ColumnType.Data );
        Column column3 = buildColumn(
                new TableName( "test.Address" ),
                "test.Address.id",
                "id",
                ColumnType.PrimaryKey );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username", Neo4jDataType.String ) )
                .add( column3, CsvField.data( "age", Neo4jDataType.String ) )
                .build();

        // when
        Collection<String> tableNames = mappings.tableNames();

        // then
        assertThat( tableNames, hasItems( "test.Person", "test.Address" ) );
    }

    private Column buildColumn( TableName table, String name, String alias, ColumnType columnType )
    {
        return new Column(
                table,
                name,
                alias,
                columnType,
                MySqlDataType.TEXT );

    }
}
