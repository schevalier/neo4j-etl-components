package org.neo4j.mysql.config;

import java.util.Collection;

import org.neo4j.ingest.config.CsvField;
import org.neo4j.ingest.config.FieldMappings;
import org.neo4j.ingest.config.QuoteChar;
import org.neo4j.utils.Preconditions;

import static java.util.Arrays.asList;

public class Join implements FieldMappings
{
    public static Builder.SetParent builder()
    {
        return new JoinBuilder();
    }

    private final Column parent;
    private final Column child;
    private final QuoteChar quote;

    Join( JoinBuilder builder )
    {
        this.parent = Preconditions.requireNonNull( builder.parent, "Parent" );
        this.child = Preconditions.requireNonNull( builder.child, "Child" );
        this.quote = Preconditions.requireNonNull( builder.quote, "Quote" );
    }

    @Override
    public Collection<CsvField> fieldMappings()
    {
        return asList( parent.field(), child.field(), CsvField.relationshipType() );
    }

    public Collection<String> columns()
    {
        return asList( parent.name(), child.name(), quote.enquote( child.table().simpleName() ) );
    }

    public Collection<TableName> tableNames()
    {
        return asList( parent.table(), child.table() );
    }

    public interface Builder
    {
        interface SetParent
        {
            SetChild parent( TableName table, String column );
        }

        interface SetChild
        {
            SetQuote child( TableName table, String column );
        }

        interface SetQuote
        {
            Builder quoteCharacter( QuoteChar quote );
        }

        Join build();
    }
}
