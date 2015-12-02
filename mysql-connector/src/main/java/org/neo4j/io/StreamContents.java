package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface StreamContents
{
    Optional<File> file() throws IOException;

    String value() throws IOException;
}
