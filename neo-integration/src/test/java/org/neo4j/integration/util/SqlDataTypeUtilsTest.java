package org.neo4j.integration.util;

import org.junit.Test;

import org.neo4j.integration.FilterOptions;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameResolver;
import org.neo4j.integration.sql.metadata.SqlDataType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SqlDataTypeUtilsTest
{
    @Test
    public void testParse() throws Exception
    {
        SqlDataType sqlDataType = SqlDataTypeUtils.parse( "float" );
        assertThat( sqlDataType, is( sqlDataType.FLOAT ) );
    }

    @Test
    public void testSetDataTypeConversion() throws Exception
    {
        SqlDataTypeUtils.setDataTypeConversion( "tinyint", Neo4jDataType.Boolean );
        assertThat( SqlDataType.TINYINT.toNeo4jDataType(), is( Neo4jDataType.Boolean ) );
    }

    @Test
    public void testSetDataTypeConversion1() throws Exception
    {
        SqlDataTypeUtils.setDataTypeConversion( SqlDataType.TINYINT, Neo4jDataType.Boolean );

        assertThat( SqlDataType.TINYINT.toNeo4jDataType(), is( Neo4jDataType.Boolean ) );

        SqlDataTypeUtils.setDataTypeConversion( SqlDataType.TINYINT, Neo4jDataType.Byte );
    }
}