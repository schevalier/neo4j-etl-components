package org.neo4j.integration.commands.mysql;

public interface CreateCsvResourcesEvents
{
    CreateCsvResourcesEvents EMPTY = new CreateCsvResourcesEvents()
    {
        @Override
        public void onCreatingCsvResourcesFile()
        {
            // Do nothing
        }

        @Override
        public void onCsvResourcesCreated()
        {
            // Do nothing
        }
    };

    void onCreatingCsvResourcesFile();

    void onCsvResourcesCreated();
}
