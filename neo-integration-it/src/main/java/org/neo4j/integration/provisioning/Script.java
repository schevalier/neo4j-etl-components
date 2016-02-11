package org.neo4j.integration.provisioning;

import java.io.IOException;

public interface Script
{
    String value() throws IOException;
}
