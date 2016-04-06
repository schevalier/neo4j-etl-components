package org.neo4j.integration.sql.metadata;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlDataTypeTest
{
    @Test
    public void parseShouldUpperCaseDataTypesToMapToMySqlDataType() throws Exception
    {
        // given
        SqlDataType anInt = SqlDataType.parse( "int" );

        // then
        assertThat( anInt, is( SqlDataType.INT ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfNumericTypes() throws Exception
    {
        assertThat( SqlDataType.INT.toNeo4jDataType(), is( Neo4jDataType.Int ) );
        assertThat( SqlDataType.TINYINT.toNeo4jDataType(), is( Neo4jDataType.Byte ) );
        assertThat( SqlDataType.SMALLINT.toNeo4jDataType(), is( Neo4jDataType.Short ) );
        assertThat( SqlDataType.MEDIUMINT.toNeo4jDataType(), is( Neo4jDataType.Int ) );
        assertThat( SqlDataType.BIGINT.toNeo4jDataType(), is( Neo4jDataType.Long ) );
        assertThat( SqlDataType.FLOAT.toNeo4jDataType(), is( Neo4jDataType.Float ) );
        assertThat( SqlDataType.DOUBLE.toNeo4jDataType(), is( Neo4jDataType.Double ) );
        assertThat( SqlDataType.DECIMAL.toNeo4jDataType(), is( Neo4jDataType.Float ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfStringTypes() throws Exception
    {
        assertThat( SqlDataType.CHAR.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.VARCHAR.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.BLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.TINYBLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.TINYTEXT.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.MEDIUMTEXT.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.MEDIUMBLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.LONGTEXT.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.LONGBLOB.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.ENUM.toNeo4jDataType(), is( Neo4jDataType.String ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfDateTypes() throws Exception
    {
        assertThat( SqlDataType.DATE.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.DATETIME.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.TIMESTAMP.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.TIME.toNeo4jDataType(), is( Neo4jDataType.String ) );
        assertThat( SqlDataType.YEAR.toNeo4jDataType(), is( Neo4jDataType.String ) );
    }

    @Test
    public void toNeo4jDataTypeMappingOfBit() throws Exception
    {
        assertThat( SqlDataType.BIT.toNeo4jDataType(), is( Neo4jDataType.Byte ) );
    }

}
