package org.neo4j.integration.sql.metadata;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class JoinTable implements DatabaseObject
{
    private final Join join;
    private final Table table;

    public JoinTable( Join join, Table table )
    {
        this.join = join;
        this.table = table;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }

    @Override
    public String descriptor()
    {
        return join.keyOneSourceColumn().table().simpleName();
    }

    @Override
    public <T> T invoke( DatabaseObjectServiceProvider<T> databaseObjectServiceProvider )
    {
        return databaseObjectServiceProvider.joinTableService( this );
    }

    public Join join()
    {
        return join;
    }

    public Collection<Column> columns()
    {
        return table.columns();
    }

    public TableName joinTableName()
    {
        return join().keyOneSourceColumn().table();
    }
}
