package org.neo4j.etl.process;

public interface CommandsSupplier
{
    void addCommandsTo( Commands.Builder.SetCommands commands );
}
