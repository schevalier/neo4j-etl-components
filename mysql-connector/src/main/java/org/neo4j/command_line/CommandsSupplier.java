package org.neo4j.command_line;

public interface CommandsSupplier
{
    void addCommandsTo( Commands.Builder.SetCommands commands );
}
