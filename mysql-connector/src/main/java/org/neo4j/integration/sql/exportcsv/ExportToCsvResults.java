package org.neo4j.integration.sql.exportcsv;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.neo4j.integration.util.Preconditions;

public class ExportToCsvResults implements Iterable<ExportToCsvResult>
{
    private final Collection<ExportToCsvResult> results;

    public ExportToCsvResults( Collection<ExportToCsvResult> results )
    {
        this.results = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection(  results, "results"));
    }

    @Override
    public Iterator<ExportToCsvResult> iterator()
    {
        return results.iterator();
    }
}
