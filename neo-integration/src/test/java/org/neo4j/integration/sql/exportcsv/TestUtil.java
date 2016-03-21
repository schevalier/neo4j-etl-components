package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.TableName;

public class TestUtil
{
    public Column column( TableName table, String nameAndAlias, ColumnType type )
    {
        return column( table, table.fullyQualifiedColumnName( nameAndAlias ), nameAndAlias, type );
    }

    public Column column( TableName table, String name, String alias, ColumnType type )
    {
        return new SimpleColumn(table, name, alias, type, MySqlDataType.TEXT );
    }
}
