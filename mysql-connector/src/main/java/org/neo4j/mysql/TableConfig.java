package org.neo4j.mysql;

import java.util.Collection;
import java.util.Collections;

import org.neo4j.utils.Preconditions;


public class TableConfig
{
    public static Builder.SetName builder()
    {
        return new TableConfigBuilder();
    }

    private final String name;
    private final Collection<Column> columns;

    TableConfig( TableConfigBuilder builder )
    {
        this.name = Preconditions.requireNonNullString( builder.name, "Table name cannot be null or empty string" );
        this.columns = Collections.unmodifiableCollection( builder.columns );
    }

    public interface Builder
    {
        interface SetName
        {
            Builder name( String name );
        }

        Builder addColumn( Column column );

        TableConfig build();
    }
}
