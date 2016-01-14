package org.neo4j.integration.mysql.exportcsv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.mysql.SqlRunner;
import org.neo4j.integration.mysql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.mysql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.mysql.exportcsv.mapping.JoinMapper;
import org.neo4j.integration.mysql.exportcsv.mapping.Mapper;
import org.neo4j.integration.mysql.exportcsv.mapping.TableMapper;
import org.neo4j.integration.mysql.exportcsv.sql.ExportSqlSupplier;
import org.neo4j.integration.mysql.exportcsv.sql.JoinExportSqlSupplier;
import org.neo4j.integration.mysql.exportcsv.sql.TableExportSqlSupplier;
import org.neo4j.integration.mysql.metadata.DatabaseObject;
import org.neo4j.integration.mysql.metadata.Join;
import org.neo4j.integration.mysql.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.HeaderFile;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfigSupplier;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class ExportToCsv
{
    private final ExportToCsvConfig config;

    public ExportToCsv( ExportToCsvConfig config )
    {
        this.config = config;
    }

    public GraphDataConfig execute() throws Exception
    {
        if ( Files.notExists( config.destination() ) )
        {
            Files.createDirectories( config.destination() );
        }

        Commands.commands( "chmod", "0777", config.destination().toString() ).execute().await();

        Collection<GraphDataConfigSupplier> graphDataConfigSuppliers = new ArrayList<>();


        try ( SqlRunner sqlRunner = new SqlRunner( config.connectionConfig() ) )
        {
            for ( Table table : config.tables() )
            {
                Collection<Path> files =
                        new ExportDatabaseContentsToCsv<Table>( sqlRunner, config )
                                .execute( table,
                                        new TableMapper( config.formatting() ),
                                        new TableExportSqlSupplier( config.formatting() ) );

                graphDataConfigSuppliers.add( NodeConfig.builder()
                        .addInputFiles( files )
                        .addLabel( table.name().simpleName() )
                        .build() );

            }

            for ( Join join : config.joins() )
            {
                Collection<Path> files =
                        new ExportDatabaseContentsToCsv<Join>( sqlRunner, config )
                                .execute( join,
                                        new JoinMapper( config.formatting() ),
                                        new JoinExportSqlSupplier( config.formatting() ) );

                graphDataConfigSuppliers.add( RelationshipConfig.builder()
                        .addInputFiles( files )
                        .build() );

            }
        }


        return new GraphDataConfig( graphDataConfigSuppliers );
    }

    private static class ExportDatabaseContentsToCsv<T extends DatabaseObject>
    {
        private final SqlRunner sqlRunner;
        private final ExportToCsvConfig config;

        private ExportDatabaseContentsToCsv( SqlRunner sqlRunner, ExportToCsvConfig config )
        {
            this.sqlRunner = sqlRunner;
            this.config = config;
        }

        public Collection<Path> execute( T source, Mapper<T> mapper, ExportSqlSupplier sqlSupplier ) throws Exception
        {
            ColumnToCsvFieldMappings mappings = mapper.createMappings( source );

            Path headerFile = new HeaderFile( config.destination(), config.formatting() )
                    .createHeaderFile( mappings.fields(), source.descriptor() );
            Path exportFile = config.destination().resolve( format( "%s.csv", source.descriptor() ) );

            sqlRunner.execute( sqlSupplier.sql( mappings, exportFile ) ).await();

            return asList( headerFile, exportFile );
        }
    }

}
