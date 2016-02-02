package org.neo4j.integration.process;

public interface CommandsSupplier
{
    void addCommandsTo( Commands.Builder.SetCommands commands );
}
