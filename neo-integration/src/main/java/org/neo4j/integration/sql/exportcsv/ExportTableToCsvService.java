package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.Table;

class ExportTableToCsvService implements ExportDatabaseObjectToCsvService
{
    private final Table table;

    public ExportTableToCsvService( Table table )
    {
        this.table = table;
    }

    @Override
    public GraphDataConfig exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                        HeaderFileWriter headerFileWriter,
                                        CsvFileWriter csvFileWriter,
                                        ExportToCsvConfig config ) throws Exception
    {
        Collection<Path> files = new CsvFilesWriter<Table>( headerFileWriter, csvFileWriter )
                .write( table, new TableToCsvFieldMapper( config.formatting() ), sqlSupplier );

        return importConfig -> importConfig.addNodeConfig( NodeConfig.builder()
                .addInputFiles( files )
                .addLabel( table.name().simpleName() )
                .build() );
    }
}
