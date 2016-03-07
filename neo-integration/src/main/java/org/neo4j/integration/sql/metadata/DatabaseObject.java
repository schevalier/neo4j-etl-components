package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;

public abstract class DatabaseObject
{
    public abstract String descriptor();

    abstract ExportToCsvResults.ExportToCsvResult exportToCsv(
            DatabaseExportSqlSupplier sqlSupplier,
            HeaderFileWriter headerFileWriter,
            CsvFileWriter csvFileWriter,
            ExportToCsvConfig config ) throws Exception;

    public boolean isJoin()
    {
        return this instanceof Join;
    }

    public boolean isJoinTable()
    {
        return this instanceof JoinTable;
    }

    public boolean isTable()
    {
        return this instanceof Table;
    }
}
