package org.neo4j.integration.sql.metadata;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.util.Preconditions;

public class Table extends DatabaseObject
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
    ExportToCsvResults.ExportToCsvResult exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                                      HeaderFileWriter headerFileWriter,
                                                      CsvFileWriter csvFileWriter,
                                                      ExportToCsvConfig config ) throws Exception
    {

        Collection<Path> files = new CsvFilesWriter<Table>( headerFileWriter, csvFileWriter )
                .write( this, new TableToCsvFieldMapper( config.formatting() ), sqlSupplier );

        return new ExportToCsvResults.ExportToCsvResult( this, files );
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
