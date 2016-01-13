package org.neo4j.integration.mysql.exportcsvnew.metadata;

import java.util.Collection;

import org.neo4j.integration.mysql.exportcsv.config.TableName;
import org.neo4j.integration.util.Preconditions;

import static java.util.Arrays.asList;

public class Join
{
    public static Builder.SetParent builder()
    {
        return new JoinBuilder();
    }

    private final Column parentKey;
    private final Column childKey;

    Join( JoinBuilder builder )
    {
        this.parentKey = Preconditions.requireNonNull( builder.parent, "Parent" );
        this.childKey = Preconditions.requireNonNull( builder.child, "Child" );
    }

    public Column parentKey()
    {
        return parentKey;
    }

    public Column childKey()
    {
        return childKey;
    }

    public Collection<TableName> tableNames()
    {
        return asList( parentKey.table(), childKey.table() );
    }

    public interface Builder
    {
        interface SetParent
        {
            SetChild parent( Column parent );
        }

        interface SetChild
        {
            Builder child( Column child );
        }

        Join build();
    }
}
