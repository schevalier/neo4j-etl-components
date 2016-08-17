package org.neo4j.etl;

import java.nio.file.Path;

import org.junit.ClassRule;
import org.junit.Test;

import org.neo4j.etl.neo4j.Neo4j;
import org.neo4j.etl.neo4j.Neo4jVersion;
import org.neo4j.etl.provisioning.Neo4jFixture;
import org.neo4j.etl.util.ResourceRule;
import org.neo4j.etl.util.TemporaryDirectory;

public class Neo4jOnWindowsTest
{
    private static final Neo4jVersion NEO4J_VERSION = Neo4jVersion.v3_0_1;

    @ClassRule
    public static final ResourceRule<Path> tempDirectory =
            new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @ClassRule
    public static final ResourceRule<Neo4j> neo4j = new ResourceRule<>(
            Neo4jFixture.neo4j( NEO4J_VERSION, tempDirectory.get() ) );

    @Test
    public void InstallNeo4j() throws Exception
    {
        neo4j.get().start();
        System.out.println(neo4j.get().binDirectory());
        neo4j.get().stop();
    }


}
