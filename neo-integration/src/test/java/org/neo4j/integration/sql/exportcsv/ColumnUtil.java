package org.neo4j.integration.sql.exportcsv;

import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

public class ColumnUtil
{
    public Column column( TableName table, String nameAndAlias, ColumnType type )
    {
        return column( table, table.fullyQualifiedColumnName( nameAndAlias ), nameAndAlias, type );
    }

    public Column primaryKeyColumn( TableName table, String nameAndAlias )
    {
        return new SimpleColumn(
                table,
                table.fullyQualifiedColumnName( nameAndAlias ),
                nameAndAlias,
                ColumnType.PrimaryKey,
                SqlDataType.KEY_DATA_TYPE );

    }

    public Column column( TableName table, String name, String alias, ColumnType type )
    {
        return new SimpleColumn( table, name, alias, type, MySqlDataType.TEXT );
    }

    public Column compositeColumn( TableName tableName, List<String> columnNames )
    {
        return new CompositeKeyColumn( tableName,
                columnNames.stream()
                        .map( name -> primaryKeyColumn( tableName, name ) )
                        .collect( Collectors.toList() ) );
    }
}
