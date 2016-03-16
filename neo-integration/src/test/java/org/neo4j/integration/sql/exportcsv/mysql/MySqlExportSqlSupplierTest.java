package org.neo4j.integration.sql.exportcsv.mysql;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;

public class MySqlExportSqlSupplierTest
{
    @Test
    public void shouldCreateSqlForSelectingColumnsFromTables()
    {
        // given
        Column column1 = new SimpleColumn(
                new TableName( "test.Person" ),
                "test.Person.id",
                "id",
                ColumnType.PrimaryKey,
                MySqlDataType.TEXT );

        Column column2 = new SimpleColumn(
                new TableName( "test.Person" ),
                "test.Person.username",
                "username",
                ColumnType.Data,
                MySqlDataType.TEXT );

        Column column3 = new SimpleColumn(
                new TableName( "test.Address" ),
                "test.Address.id",
                "id",
                ColumnType.PrimaryKey,
                MySqlDataType.TEXT );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( column1, CsvField.id() )
                .add( column2, CsvField.data( "username", Neo4jDataType.String ) )
                .add( column3, CsvField.data( "age", Neo4jDataType.String ) )
                .build();

        MySqlExportSqlSupplier sqlSupplier = new MySqlExportSqlSupplier();

        // when
        String sql = sqlSupplier.sql( mappings );

        // then
        String expectedSql = "SELECT " +
                "test.Person.id AS id, " +
                "test.Person.username AS username, " +
                "test.Address.id AS id " +
                "FROM test.Person, test.Address";

        assertEquals( expectedSql, sql );
    }
}
