package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.util.Preconditions;

public class ExportToCsvResult
{
    private final DatabaseObject databaseObject;
    private final Collection<Path> csvFiles;

    public ExportToCsvResult( DatabaseObject databaseObject, Collection<Path> csvFiles )
    {
        this.databaseObject = Preconditions.requireNonNull( databaseObject, "databaseObject" );
        this.csvFiles = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( csvFiles, "csvFiles" ) );
    }

    public DatabaseObject databaseObject()
    {
        return databaseObject;
    }

    public Collection<Path> csvFiles()
    {
        return csvFiles;
    }
}
