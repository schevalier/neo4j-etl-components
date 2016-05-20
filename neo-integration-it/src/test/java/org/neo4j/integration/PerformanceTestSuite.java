package org.neo4j.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses(value = BigPerformanceTest.class)
public class PerformanceTestSuite
{
}
