package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;

public interface SqlDataType
{
    SqlDataType LABEL_DATA_TYPE = () -> Neo4jDataType.String;
    SqlDataType RELATIONSHIP_TYPE_DATA_TYPE = () -> Neo4jDataType.String;

    Neo4jDataType toNeo4jDataType();
}
