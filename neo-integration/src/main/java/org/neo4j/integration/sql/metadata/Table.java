package org.neo4j.integration.sql.metadata;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.util.Preconditions;

public class Table implements DatabaseObject
{
    public static Builder.SetName builder()
    {
        return new TableBuilder();
    }

    private final TableName name;
    private final Collection<Column> columns;

    Table( TableBuilder builder )
    {
        this.name = Preconditions.requireNonNull( builder.table, "Name" );
        this.columns = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.columns, "Columns" ) );
    }

    public TableName name()
    {
        return name;
    }

    public Collection<Column> columns()
    {
        return columns;
    }

    @Override
    public String descriptor()
    {
        return name.fullName();
    }

    @Override
    public <T> T exportService( ExportServiceProvider<T> exportServiceProvider )
    {
        return exportServiceProvider.tableExportService( this );
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    public interface Builder
    {
        interface SetName
        {
            Builder name( String name );

            Builder name( TableName name );
        }

        Builder addColumn( Column column );

        Table build();
    }
}
