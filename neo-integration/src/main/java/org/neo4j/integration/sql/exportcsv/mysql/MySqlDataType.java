package org.neo4j.integration.sql.exportcsv.mysql;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.sql.metadata.SqlDataType;

public enum MySqlDataType implements SqlDataType
{
    INT( Neo4jDataType.Int ),
    TINYINT( Neo4jDataType.Byte ),
    SMALLINT( Neo4jDataType.Short ),
    BIGINT( Neo4jDataType.Long ),
    FLOAT( Neo4jDataType.Float ),
    DOUBLE( Neo4jDataType.Double ),
    DECIMAL( Neo4jDataType.Float ), //unconfirmed
    MEDIUMINT( Neo4jDataType.Int ), //unconfirmed

    CHAR( Neo4jDataType.String ),
    VARCHAR( Neo4jDataType.String ),
    TEXT( Neo4jDataType.String ),
    BLOB( Neo4jDataType.String ),
    TINYBLOB( Neo4jDataType.String ),
    TINYTEXT( Neo4jDataType.String ),
    MEDIUMTEXT( Neo4jDataType.String ),
    MEDIUMBLOB( Neo4jDataType.String ),
    LONGTEXT( Neo4jDataType.String ),
    LONGBLOB( Neo4jDataType.String ),
    ENUM( Neo4jDataType.String );

    private final Neo4jDataType neo4jDataType;

    MySqlDataType( Neo4jDataType neo4jDataType )
    {
        this.neo4jDataType = neo4jDataType;
    }

    public static MySqlDataType parse( String value )
    {
        return valueOf( value.toUpperCase() );
    }

    @Override
    public Neo4jDataType toNeo4jDataType()
    {
        return neo4jDataType;
    }
}
