package org.neo4j.integration.sql.metadata;

import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinToCsvFieldMapper;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Join implements DatabaseObject
{
    public static Builder.SetParentTable builder()
    {
        return new JoinBuilder();
    }

    private final Column primaryKey;
    private final Column foreignKey;
    private final TableName childTable;
    private final TableName startTable;

    Join( JoinBuilder builder )
    {
        this.primaryKey = Preconditions.requireNonNull( builder.primaryKey, "PrimaryKey" );
        this.foreignKey = Preconditions.requireNonNull( builder.foreignKey, "ForeignKey" );
        this.childTable = Preconditions.requireNonNull( builder.childTable, "ChildTable" );
        this.startTable = Preconditions.requireNonNull( builder.startTable, "StartTable" );
    }

    public boolean childTableRepresentsStartOfRelationship()
    {
        return startTable.equals( childTable );
    }

    public boolean parentTableRepresentsStartOfRelationship()
    {
        return startTable.equals( primaryKey.table() );
    }

    public Column primaryKey()
    {
        return primaryKey;
    }

    public Column foreignKey()
    {
        return foreignKey;
    }

    public TableName childTable()
    {
        return childTable;
    }

    public Collection<TableName> tableNames()
    {
        return asList( primaryKey.table(), childTable );
    }

    @Override
    public String descriptor()
    {
        return format( "%s_%s", primaryKey.table().fullName(), childTable.fullName() );
    }

    @Override
    public <T> T exportService( ExportServiceProvider<T> exportServiceProvider )
    {
        return exportServiceProvider.joinExportService( this );
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    public interface Builder
    {
        interface SetParentTable
        {
            SetPrimaryKey parentTable( TableName parent );
        }

        interface SetPrimaryKey
        {
            SetForeignKey primaryKey( String primaryKey );
        }

        interface SetForeignKey
        {
            SetChildTable foreignKey( String foreignKey );
        }

        interface SetChildTable
        {
            SetStartTable childTable( TableName childTable );
        }

        interface SetStartTable
        {
            Builder startTable( TableName startTable );
        }

        Join build();
    }
}
