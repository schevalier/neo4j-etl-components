package org.neo4j.integration.environment;

import java.nio.file.Path;

public class Environment
{
    private final Path importToolDirectory;
    private final Path destinationDirectory;
    private final Path csvDirectory;

    public Environment( Path importToolDirectory, Path destinationDirectory, Path csvDirectory )
    {
        this.importToolDirectory = importToolDirectory;
        this.destinationDirectory = destinationDirectory;
        this.csvDirectory = csvDirectory;
    }

    public Path importToolDirectory()
    {
        return importToolDirectory;
    }

    public Path destinationDirectory()
    {
        return destinationDirectory;
    }

    public Path csvDirectory()
    {
        return csvDirectory;
    }
}
