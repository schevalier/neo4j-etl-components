package org.neo4j.integration.sql.exportcsv.io;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.exportcsv.mapping.TinyIntAs;
import org.neo4j.integration.sql.metadata.SqlDataType;

public class TinyIntResolver
{
    private TinyIntAs tinyIntAs;

    public TinyIntResolver( TinyIntAs tinyIntAs )
    {
        this.tinyIntAs = tinyIntAs;
        SqlDataType.TINYINT.setNeoDataType( this.tinyIntAs.neoDataType() );
    }

    public String handleSpecialCaseForTinyInt( String value, SqlDataType sqlDataType )
    {
        if ( TinyIntAs.BOOLEAN.equals( tinyIntAs ) && sqlDataType.equals( SqlDataType.TINYINT ) )
        {
            return Integer.parseInt( value ) == 0 ? "false" : "true";
        }
        return value;
    }

    public Neo4jDataType targetDataType( SqlDataType sqlDataType )
    {
        if ( TinyIntAs.BOOLEAN.equals( tinyIntAs ) && sqlDataType.equals( SqlDataType.TINYINT ) )
        {
            return Neo4jDataType.Boolean;
        }
        return sqlDataType.toNeo4jDataType();
    }
}
