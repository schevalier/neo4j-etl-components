package org.neo4j.integration.sql.exportcsv.mysql;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.metadata.SqlDataType;

public enum MySqlDataType implements SqlDataType
{
    INT( Neo4jDataType.Int ),

    TEXT( Neo4jDataType.String );

    private final Neo4jDataType neo4jDataType;

    public static MySqlDataType parse( String value )
    {
        return valueOf( value.toUpperCase() );
    }

    MySqlDataType( Neo4jDataType neo4jDataType )
    {
        this.neo4jDataType = neo4jDataType;
    }

    @Override
    public Neo4jDataType toNeo4jDataType()
    {
        return neo4jDataType;
    }
}
