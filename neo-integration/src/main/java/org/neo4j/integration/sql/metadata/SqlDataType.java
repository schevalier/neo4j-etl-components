package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;

//public interface SqlDataType
//{
//    SqlDataType COMPOSITE_KEY_TYPE = () -> Neo4jDataType.String;
//    SqlDataType LABEL_DATA_TYPE = () -> Neo4jDataType.String;
//    SqlDataType RELATIONSHIP_TYPE_DATA_TYPE = () -> Neo4jDataType.String;
//    SqlDataType KEY_DATA_TYPE = () -> Neo4jDataType.String;
//
//    Neo4jDataType toNeo4jDataType();
//}


public enum SqlDataType
{
    BIT( Neo4jDataType.Byte ),
    INT( Neo4jDataType.Int ),
    TINYINT( Neo4jDataType.Byte ),
    SMALLINT( Neo4jDataType.Short ),
    BIGINT( Neo4jDataType.Long ),
    FLOAT( Neo4jDataType.Float ),
    DOUBLE( Neo4jDataType.Double ),
    DECIMAL( Neo4jDataType.Float ),
    MEDIUMINT( Neo4jDataType.Int ),

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
    ENUM( Neo4jDataType.String ),

    DATE(Neo4jDataType.String ),
    DATETIME( Neo4jDataType.String ),
    TIMESTAMP( Neo4jDataType.String ),
    TIME( Neo4jDataType.String ),
    YEAR( Neo4jDataType.String );

    public static SqlDataType parse( String value )
    {
        return valueOf( value.toUpperCase() );
    }

    public static final SqlDataType COMPOSITE_KEY_TYPE = TEXT;
    public static final SqlDataType LABEL_DATA_TYPE = TEXT;
    public static final SqlDataType RELATIONSHIP_TYPE_DATA_TYPE = TEXT;
    public static final SqlDataType KEY_DATA_TYPE = TEXT;

    private final Neo4jDataType neo4jDataType;

    SqlDataType( Neo4jDataType neo4jDataType )
    {
        this.neo4jDataType = neo4jDataType;
    }

    public Neo4jDataType toNeo4jDataType()
    {
        return neo4jDataType;
    }
}
