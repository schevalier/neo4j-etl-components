package org.neo4j.integration.sql.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.io.TinyIntResolver;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMapping;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class SimpleColumn implements Column
{
    public static Column fromJson( JsonNode root )
    {
        return new SimpleColumn(
                new TableName( root.path( "table" ).textValue() ),
                root.path( "name" ).textValue(),
                root.path( "alias" ).textValue(),
                ColumnRole.valueOf( root.path( "role" ).textValue() ),
                SqlDataType.valueOf( root.path( "sql-data-type" ).textValue() ),
                ColumnValueSelectionStrategy.valueOf( root.path( "column-value-selection-strategy" ).textValue() ) );
    }

    private final TableName table;
    private final String name;
    private final String alias;
    private final ColumnRole role;
    private final SqlDataType dataType;
    private final ColumnValueSelectionStrategy columnValueSelectionStrategy;

    public SimpleColumn( TableName table,
                         String name,
                         ColumnRole role,
                         SqlDataType dataType,
                         ColumnValueSelectionStrategy columnValueSelectionStrategy )
    {
        this( table, name, name, role, dataType, columnValueSelectionStrategy );
    }

    public SimpleColumn( TableName table,
                         String name,
                         String alias,
                         ColumnRole role,
                         SqlDataType dataType,
                         ColumnValueSelectionStrategy columnValueSelectionStrategy )
    {
        this.table = Preconditions.requireNonNull( table, "Table" );
        this.name = Preconditions.requireNonNullString( name, "Name" );
        this.alias = Preconditions.requireNonNullString( alias, "Alias" );
        this.role = Preconditions.requireNonNull( role, "Role" );
        this.dataType = Preconditions.requireNonNull( dataType, "DataType" );
        this.columnValueSelectionStrategy =
                Preconditions.requireNonNull( columnValueSelectionStrategy, "ColumnValueSelectionStrategy" );
    }

    @Override
    public TableName table()
    {
        return table;
    }

    // Fully-qualified column name, or literal value
    @Override
    public String name()
    {
        return role == ColumnRole.Literal ? name : table.fullyQualifiedColumnName( name );
    }

    // Column alias
    @Override
    public String alias()
    {
        return alias;
    }

    @Override
    public ColumnRole role()
    {
        return role;
    }

    @Override
    public SqlDataType sqlDataType()
    {
        return dataType;
    }

    @Override
    public boolean allowAddToSelectStatement()
    {
        return columnValueSelectionStrategy.allowAddToSelectStatement();
    }

    @Override
    public String selectFrom( RowAccessor row, int rowIndex )
    {
        return columnValueSelectionStrategy.selectFrom( row, rowIndex, alias );
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
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    @Override
    public String aliasedColumn()
    {
        if ( role == ColumnRole.Literal )
        {
            return format( "%s AS `%s`", name(), alias );
        }
        else
        {
            String nameWithTicks = StringUtils.join( name().split( "\\." ), "`.`" );
            return format( "`%s` AS `%s`", nameWithTicks, alias );
        }
    }

    @Override
    public void addData( ColumnToCsvFieldMappings.Builder builder, TinyIntResolver tinyIntResolver )
    {
        builder.add( new ColumnToCsvFieldMapping( this, CsvField.data( alias,
                tinyIntResolver.targetDataType( dataType ) ) ) );
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "type", getClass().getSimpleName() );
        root.put( "role", role.name() );
        root.put( "table", table.fullName() );
        root.put( "name", name );
        root.put( "alias", alias );
        root.put( "sql-data-type", dataType.name() );
        root.put( "column-value-selection-strategy", columnValueSelectionStrategy.name() );

        return root;
    }

    @Override
    public boolean useQuotes()
    {
        return dataType.toNeo4jDataType().shouldUseQuotes();
    }
}
