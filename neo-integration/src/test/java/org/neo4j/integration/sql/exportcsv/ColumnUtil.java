package org.neo4j.integration.sql.exportcsv;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.CompositeColumn;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

public class ColumnUtil
{
    public Column column( TableName table, String nameAndAlias, ColumnRole type )
    {
        return column( table, nameAndAlias, nameAndAlias, type );
    }

    public Column column( TableName table, String name, String alias, ColumnRole role )
    {
        return new SimpleColumn( table, name, alias, EnumSet.of( role ), SqlDataType.TEXT );
    }

    public SimpleColumn keyColumn( TableName tableName, String nameAndAlias, ColumnRole role )
    {
        return new SimpleColumn(
                tableName,
                nameAndAlias,
                nameAndAlias,
                EnumSet.of( role ),
                SqlDataType.KEY_DATA_TYPE );
    }

    public Column compositeKeyColumn( TableName tableName, List<String> columnNames, ColumnRole keyType )
    {
        return new CompositeColumn( tableName,
                columnNames.stream()
                        .map( name -> keyColumn( tableName, name, keyType ) )
                        .collect( Collectors.toList() ) );
    }
}
