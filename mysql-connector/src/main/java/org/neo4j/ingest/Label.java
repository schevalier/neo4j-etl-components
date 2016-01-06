package org.neo4j.ingest;

public class Label implements FieldType
{
    public static Label label()
    {
        return new Label();
    }

    private Label()
    {
    }

    @Override
    public void validate( boolean fieldHasName )
    {
        // Do nothing
    }
}
