package org.neo4j.integration.sql.exportcsv.mysql;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.TestUtil;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;

public class MySqlExportSqlSupplierTest
{

    private TestUtil testUtil = new TestUtil();

    @Test
    public void shouldCreateSqlForSelectingColumnsFromTables()
    {
        // given
        Column column1 = testUtil.column( new TableName( "test.Person" ), "id", ColumnType.PrimaryKey );

        Column column2 = testUtil.column( new TableName( "test.Person" ),"username", ColumnType.Data );

        Column column3 = testUtil.column( new TableName( "test.Address" ),"id", ColumnType.PrimaryKey );

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
