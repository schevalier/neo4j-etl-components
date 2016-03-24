package org.neo4j.integration.sql.metadata;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/*
Represents a database join.

Simple joins are represented like this:

+----------------------------+      +-------------------+
| PK (key1, source + target) |----->| PK (key2, target) |
| FK (key2, source)          |      |                   |
+----------------------------+      +-------------------+

Join table joins are represented like this:

+-------------------+      +-------------------+      +-------------------+
| PK (key1, target) |<-----| FK (key1, source) |----->| FK (key2, target) |
|                   |      | FK (key2, source) |      |                   |
+-------------------+      +-------------------+      +-------------------+

 */
public class Join implements DatabaseObject
{
    private final JoinKey keyOne;
    private final JoinKey keyTwo;

    public Join( JoinKey keyOne, JoinKey keyTwo )
    {
        this.keyOne = Preconditions.requireNonNull( keyOne, "KeyOne" );
        this.keyTwo = Preconditions.requireNonNull( keyTwo, "KeyTwo" );
    }

    public Column keyOneSourceColumn()
    {
        return keyOne.sourceColumn();
    }

    public Column keyTwoSourceColumn()
    {
        return keyTwo.sourceColumn();
    }

    public Column keyOneTargetColumn()
    {
        return keyOne.targetColumn();
    }

    public Column keyTwoTargetColumn()
    {
        return keyTwo.targetColumn();
    }

    public Collection<TableName> tableNames()
    {
        return asList( keyOne.sourceColumn().table(), keyTwo.targetColumn().table() );
    }

    @Override
    public String descriptor()
    {
        return format( "%s_%s", keyOne.sourceColumn().table().fullName(), keyTwoTargetColumn().table().fullName() );
    }

    @Override
    public <T> T createService( DatabaseObjectServiceProvider<T> databaseObjectServiceProvider )
    {
        return databaseObjectServiceProvider.joinService( this );
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
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
}
