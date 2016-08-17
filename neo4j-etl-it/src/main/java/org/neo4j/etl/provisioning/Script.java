package org.neo4j.etl.provisioning;

import java.io.IOException;

public interface Script
{
    String value() throws IOException;
}
