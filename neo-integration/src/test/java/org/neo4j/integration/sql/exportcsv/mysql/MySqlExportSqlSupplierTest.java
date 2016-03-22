package org.neo4j.integration.sql.exportcsv.mysql;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.TestUtil;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;

public class MySqlExportSqlSupplierTest
{

    private TestUtil testUtil = new TestUtil();

    @Test
    @Ignore
    public void shouldCreateSqlForSelectingColumnsFromTables()
    {
        // given
        Column column1 = testUtil.column( new TableName( "test.Person" ), "id", ColumnType.PrimaryKey );

        Column column2 = testUtil.column( new TableName( "test.Person" ), "username", ColumnType.Data );

        Column column3 = testUtil.column( new TableName( "test.Address" ), "id", ColumnType.PrimaryKey );

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
        expectedSql = "SELECT test.Person.id AS id, " +
                "test.Address.id AS id, " +
                "test.Person.username AS username " +
                "FROM test.Person, test.Address";

        assertEquals( expectedSql, sql );
    }

    @Test
    public void shouldCreateSqlForCompositeColumnsFromTables()
    {
        // given
        TableName forTable = new TableName( "test.Author" );
        Column firstName = testUtil.column(
                forTable, "first_name", ColumnType.PrimaryKey );

        Column lastName = testUtil.column( forTable, "last_name", ColumnType.PrimaryKey );

        ColumnToCsvFieldMappings mappings = ColumnToCsvFieldMappings.builder()
                .add( new CompositeKeyColumn(
                                forTable, Arrays.asList( firstName, lastName ) ),
                        CsvField.id() )
                .build();

        MySqlExportSqlSupplier sqlSupplier = new MySqlExportSqlSupplier();

        // when
        String sql = sqlSupplier.sql( mappings );

        // then
        String expectedSql = "SELECT test.Author.first_name AS first_name, " +
                "test.Author.last_name AS last_name " +
                "FROM test.Author";

        assertEquals( expectedSql, sql );
    }
}
