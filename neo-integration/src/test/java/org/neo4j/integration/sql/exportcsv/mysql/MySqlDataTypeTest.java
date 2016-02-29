package org.neo4j.integration.sql.exportcsv.mysql;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MySqlDataTypeTest
{
    @Test
    public void parseShouldUpperCaseDataTypesToMapToMySqlDataType() throws Exception
    {
        // given
        MySqlDataType anInt = MySqlDataType.parse( "int" );

        // then
        assertThat( anInt, is( MySqlDataType.INT ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfNumericTypes() throws Exception
    {
        assertThat( MySqlDataType.INT.toNeo4jDataType(), is( Neo4jDataType.Int ) );
        assertThat( MySqlDataType.TINYINT.toNeo4jDataType(), is( Neo4jDataType.Byte ) );
        assertThat( MySqlDataType.SMALLINT.toNeo4jDataType(), is( Neo4jDataType.Short ) );
        assertThat( MySqlDataType.MEDIUMINT.toNeo4jDataType(), is( Neo4jDataType.Int ) );
        assertThat( MySqlDataType.BIGINT.toNeo4jDataType(), is( Neo4jDataType.Long ) );
        assertThat( MySqlDataType.FLOAT.toNeo4jDataType(), is( Neo4jDataType.Float ) );
        assertThat( MySqlDataType.DOUBLE.toNeo4jDataType(), is( Neo4jDataType.Double ) );
        assertThat( MySqlDataType.DECIMAL.toNeo4jDataType(), is( Neo4jDataType.Float ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfStringTypes() throws Exception
    {
        assertThat( MySqlDataType.CHAR.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.VARCHAR.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.BLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.TINYBLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.TINYTEXT.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.MEDIUMTEXT.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.MEDIUMBLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.LONGTEXT.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.LONGBLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.ENUM.toNeo4jDataType(), is( Neo4jDataType.String ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfDateTypes() throws Exception
    {
        assertThat( MySqlDataType.DATE.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.DATETIME.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.TIMESTAMP.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.TIME.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( MySqlDataType.YEAR.toNeo4jDataType(), is( Neo4jDataType.String ) );
    }
}
