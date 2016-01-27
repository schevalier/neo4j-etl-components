package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.Mapper;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.neo4j.importcsv.HeaderFileWriter;

import static java.util.Arrays.asList;

public class CsvWriter<T extends DatabaseObject>
{
    private final HeaderFileWriter headerFileWriter;
    private final ExportFileWriter exportFileWriter;

    public CsvWriter( HeaderFileWriter headerFileWriter, ExportFileWriter exportFileWriter )
    {
        this.headerFileWriter = headerFileWriter;
        this.exportFileWriter = exportFileWriter;
    }

    public Collection<Path> write( T source,
                                   Mapper<T> mapper,
                                   ExportSqlSupplier sqlSupplier ) throws Exception
    {
        ColumnToCsvFieldMappings mappings = mapper.createMappings( source );

        Path headerFile = headerFileWriter.writeHeaderFile( mappings.fields(), source.descriptor() );
        Path exportFile = exportFileWriter.writeExportFile( mappings, sqlSupplier, source.descriptor() );

        return asList( headerFile, exportFile );
    }
}
