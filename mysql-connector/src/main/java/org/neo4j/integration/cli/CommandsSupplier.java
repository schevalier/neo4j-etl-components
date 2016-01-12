package org.neo4j.integration.cli;

public interface CommandsSupplier
{
    void addCommandsTo( Commands.Builder.SetCommands commands );
}
