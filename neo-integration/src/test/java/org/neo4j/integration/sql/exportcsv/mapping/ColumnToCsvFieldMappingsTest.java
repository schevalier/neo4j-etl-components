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
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.id" )
                .alias( "id" )
                .columnType( ColumnType.PrimaryKey ).dataType( MySqlDataType.TEXT ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.username" )
                .alias( "username" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();

        Column column3 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.age" )
                .alias( "age" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();

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
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.id" )
                .alias( "id" )
                .columnType( ColumnType.PrimaryKey ).dataType( MySqlDataType.TEXT ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.username" )
                .alias( "username" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();
        ;

        Column column3 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.age" )
                .alias( "age" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();
        ;

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
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.id" )
                .alias( "id" )
                .columnType( ColumnType.PrimaryKey ).dataType( MySqlDataType.TEXT ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.username" )
                .alias( "username" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();
        ;

        Column column3 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.age" )
                .alias( "age" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();
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
        Column column1 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.id" )
                .alias( "id" )
                .columnType( ColumnType.PrimaryKey ).dataType( MySqlDataType.TEXT ).build();

        Column column2 = Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "test.Person.username" )
                .alias( "username" )
                .columnType( ColumnType.Data ).dataType( MySqlDataType.TEXT ).build();
        ;

        Column column3 = Column.builder()
                .table( new TableName( "test.Address" ) )
                .name( "test.Address.id" )
                .alias( "id" )
                .columnType( ColumnType.PrimaryKey ).dataType( MySqlDataType.TEXT ).build();

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
}
