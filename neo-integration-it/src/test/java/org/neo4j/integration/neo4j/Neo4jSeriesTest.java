package org.neo4j.integration.neo4j;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

import static java.lang.String.format;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class Neo4jSeriesTest
{
    @Test
    public void shouldKnowWhenItIncludesAVersion()
    {
        assertThat( Neo4jVersion.v1_9_2, includedIn( Neo4jSeries.v1_9_x ) );
        assertThat( Neo4jVersion.v2_0_3, includedIn( Neo4jSeries.v2_x_x ) );
    }

    @Test
    public void shouldKnowWhenItDoesntIncludeAVersion()
    {
        assertThat( Neo4jVersion.v1_9_2, not( includedIn( Neo4jSeries.v2_x_x ) ) );
        assertThat( Neo4jVersion.v2_1_3, not( includedIn( Neo4jSeries.v2_2_x ) ) );
    }

    @Test
    public void shouldKnowWhenItPrecedesAVersion()
    {
        assertThat( Neo4jVersion.v2_2_0, precededBy( Neo4jSeries.v2_1_x ) );
        assertThat( Neo4jVersion.from( "3.1.3" ), precededBy( Neo4jSeries.v2_x_x ) );
    }

    @Test
    public void shouldKnowWhenItDoesntPrecedeAVersion()
    {
        assertThat( Neo4jVersion.v2_1_3, not( precededBy( Neo4jSeries.v2_1_x ) ) );
        assertThat( Neo4jVersion.v2_1_3, not( precededBy( Neo4jSeries.v2_2_x ) ) );

        assertThat( Neo4jVersion.v2_2_0, not( precededBy( Neo4jSeries.v2_x_x ) ) );
        assertThat( Neo4jVersion.v1_9_0, not( precededBy( Neo4jSeries.v2_x_x ) ) );
    }

    private Matcher<Neo4jVersion> precededBy( final Neo4jSeries series )
    {
        return new CustomTypeSafeMatcher<Neo4jVersion>( format( "preceeded by %s", series ) )
        {
            @Override
            protected boolean matchesSafely( Neo4jVersion version )
            {
                return series.precedes( version );
            }
        };
    }

    private Matcher<Neo4jVersion> includedIn( final Neo4jSeries series )
    {
        return new CustomTypeSafeMatcher<Neo4jVersion>( format( "included in %s", series ) )
        {
            @Override
            protected boolean matchesSafely( Neo4jVersion version )
            {
                return series.includes( version );
            }
        };
    }
}
