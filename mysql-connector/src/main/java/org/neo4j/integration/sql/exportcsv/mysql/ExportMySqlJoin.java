package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.HeaderFileWriter;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.exportcsv.CsvWriter;
import org.neo4j.integration.sql.exportcsv.ExportFileWriter;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.JoinMapper;
import org.neo4j.integration.sql.metadata.Join;

class ExportMySqlJoin
{
    private final Join join;
    private final HeaderFileWriter headerFileWriter;
    private final ExportFileWriter exportFileWriter;
    private final ExportToCsvConfig config;

    ExportMySqlJoin( Join join,
                     HeaderFileWriter headerFileWriter,
                     ExportFileWriter exportFileWriter,
                     ExportToCsvConfig config )
    {
        this.join = join;
        this.headerFileWriter = headerFileWriter;
        this.exportFileWriter = exportFileWriter;
        this.config = config;
    }

    public GraphDataConfig export() throws Exception
    {
        Collection<Path> files =
                new CsvWriter<Join>( headerFileWriter, exportFileWriter )
                        .write( join,
                                new JoinMapper( config.formatting() ),
                                new MySqlJoinExportSqlSupplier( config.formatting() ) );

        return RelationshipConfig.builder()
                .addInputFiles( files )
                .build();
    }
}
