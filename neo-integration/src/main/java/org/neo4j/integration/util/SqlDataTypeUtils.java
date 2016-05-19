package org.neo4j.integration.util;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.metadata.SqlDataType;

import static java.lang.String.format;

public class SqlDataTypeUtils
{
    public static SqlDataType parse( String dataType )
    {
        try
        {
            return SqlDataType.valueOf( dataType.toUpperCase() );
        }
        catch ( NullPointerException e )
        {
            throw new IllegalArgumentException( format( "Unrecognized SQL data type: %s", dataType ) );
        }
    }

    public static void setDataTypeConversion( String originDataType, Neo4jDataType neoDataType )
    {
        setDataTypeConversion( parse( originDataType ), neoDataType );
    }

    public static void setDataTypeConversion( SqlDataType originDataType, Neo4jDataType neoDataType )
    {
        originDataType.setNeoDataType( neoDataType );
    }
}
