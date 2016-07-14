package org.neo4j.integration.sql.exportcsv.io;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.mapping.TinyIntAs;
import org.neo4j.integration.sql.metadata.SqlDataType;

import static org.junit.Assert.assertEquals;

public class TinyIntResolverTest
{
    private static final TinyIntResolver byteResolver = new TinyIntResolver( TinyIntAs.BYTE );
    private static final TinyIntResolver booleanResolver = new TinyIntResolver( TinyIntAs.BOOLEAN );

    @Test
    public void byteResolverShouldNotTransformToBoolean() throws Exception
    {
        assertEquals( "1", byteResolver.handleSpecialCaseForTinyInt( "1", SqlDataType.INT ) );
        assertEquals( "1", byteResolver.handleSpecialCaseForTinyInt( "1", SqlDataType.VARCHAR ) );
        assertEquals( "1", byteResolver.handleSpecialCaseForTinyInt( "1", SqlDataType.TINYINT ) );
        assertEquals( "0", byteResolver.handleSpecialCaseForTinyInt( "0", SqlDataType.TINYINT ) );
    }

    @Test
    public void booleanResolverShouldTransformTinyIntToBoolean() throws Exception
    {
        assertEquals( "1", booleanResolver.handleSpecialCaseForTinyInt( "1", SqlDataType.INT ) );
        assertEquals( "1", booleanResolver.handleSpecialCaseForTinyInt( "1", SqlDataType.VARCHAR ) );
        assertEquals( "true", booleanResolver.handleSpecialCaseForTinyInt( "1", SqlDataType.TINYINT ) );
        assertEquals( "false", booleanResolver.handleSpecialCaseForTinyInt( "0", SqlDataType.TINYINT ) );
    }

    @Test
    public void byteResolverShouldReturnTargetDataTypeAsIs() throws Exception
    {
        assertEquals( Neo4jDataType.Int, byteResolver.targetDataType( SqlDataType.INT ) );
        assertEquals( Neo4jDataType.String, byteResolver.targetDataType( SqlDataType.VARCHAR ) );
        assertEquals( Neo4jDataType.Byte, byteResolver.targetDataType( SqlDataType.TINYINT ) );
    }

    @Test
    public void booleanResolverShouldReturnTargetDataTypeAsBooleanForTinyInt() throws Exception
    {
        assertEquals( Neo4jDataType.Int, booleanResolver.targetDataType( SqlDataType.INT ) );
        assertEquals( Neo4jDataType.String, booleanResolver.targetDataType( SqlDataType.VARCHAR ) );
        assertEquals( Neo4jDataType.Boolean, booleanResolver.targetDataType( SqlDataType.TINYINT ) );
    }
}
