package org.neo4j.integration.provisioning;

import java.io.IOException;

public interface StartupScript
{
    String value() throws IOException;
}
