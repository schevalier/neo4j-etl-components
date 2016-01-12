package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.ingest.config.CsvField;
import org.neo4j.ingest.config.Delimiter;
import org.neo4j.ingest.config.FieldMappings;
import org.neo4j.ingest.config.QuoteChar;
import org.neo4j.utils.Preconditions;

import static java.util.Arrays.asList;

import static org.neo4j.utils.StringListBuilder.stringList;

public class Join implements FieldMappings, SqlSupplier
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

    @Override
    public String sql( Path exportFile, Delimiter delimiter )
    {
        return "SELECT " +
                stringList( columns(), delimiter.value() ) +
                " INTO OUTFILE '" +
                exportFile.toString() +
                "' FIELDS TERMINATED BY '" +
                delimiter.value() +
                "' OPTIONALLY ENCLOSED BY ''" +
                " ESCAPED BY '\\\\'" +
                " LINES TERMINATED BY '\\n'" +
                " STARTING BY ''" +
                " FROM " + stringList( tableNames(), ", ", TableName::fullName );
    }

    public Collection<TableName> tableNames()
    {
        return asList( parent.table(), child.table() );
    }

    private Collection<String> columns()
    {
        return asList( parent.name(), child.name(), quote.enquote( child.table().simpleName() ) );
    }

    public interface Builder
    {
        interface SetParent
        {
            SetChild parent( TableName table, String foreignKey );
        }

        interface SetChild
        {
            SetQuote child( TableName table, String primaryKey );
        }

        interface SetQuote
        {
            Builder quoteCharacter( QuoteChar quote );
        }

        Join build();
    }
}
