package org.neo4j.integration.sql.metadata;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinTableToCsvFieldMapper;
import org.neo4j.integration.util.Preconditions;

public class JoinTable extends DatabaseObject
{
    public static Builder.SetStartForeignKey builder()
    {
        return new JoinTableBuilder();
    }

    private final Column startForeignKey;
    private final Column startPrimaryKey;
    private final Column endPrimaryKey;
    private final Column endForeignKey;

    public JoinTable( JoinTableBuilder builder )
    {
        this.startForeignKey = Preconditions.requireNonNull(builder.startForeignKey, "StartForeignKey");
        this.startPrimaryKey = Preconditions.requireNonNull(builder.startPrimaryKey, "StartPrimaryKey");
        this.endPrimaryKey = Preconditions.requireNonNull(builder.endPrimaryKey, "EndPrimaryKey");
        this.endForeignKey = Preconditions.requireNonNull(builder.endForeignKey, "EndForeignKey");
    }

    @Override
    public String descriptor()
    {
        return startForeignKey.table().simpleName();
    }

    @Override
    ExportToCsvResults.ExportToCsvResult exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                                      HeaderFileWriter headerFileWriter,
                                                      CsvFileWriter csvFileWriter,
                                                      ExportToCsvConfig config ) throws Exception
    {
        Collection<Path> files = new CsvFilesWriter<JoinTable>( headerFileWriter, csvFileWriter )
                .write( this, new JoinTableToCsvFieldMapper( config.formatting() ), sqlSupplier );

        return new ExportToCsvResults.ExportToCsvResult( this, files );
    }

    public Column startForeignKey()
    {
        return startForeignKey;
    }

    public Column startPrimaryKey()
    {
        return startPrimaryKey;
    }

    public Column endForeignKey()
    {
        return endForeignKey;
    }

    public Column endPrimaryKey()
    {
        return endPrimaryKey;
    }

    public TableName joinTableName()
    {
        return startForeignKey.table();
    }

    public interface Builder
    {
        interface SetStartForeignKey
        {
            SetStartPrimaryKey startForeignKey( Column startForeignKey );
        }

        interface SetStartPrimaryKey
        {
            SetEndForeignKey connectsToStartTablePrimaryKey( Column startPrimaryKey );
        }

        interface SetEndForeignKey
        {
            SetEndPrimaryKey endForeignKey( Column endForeignKey );
        }

        interface SetEndPrimaryKey
        {
            Builder connectsToEndTablePrimaryKey( Column endPrimaryKey );
        }

        JoinTable build();
    }
}
