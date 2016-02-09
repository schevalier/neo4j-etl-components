package org.neo4j.integration.platforms.aws;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class MySqlCloudFormationTemplateTest
{
    @Test
    @Ignore
    public void shouldStartCloudFormationStack() throws IOException
    {
        // given
        MySqlCloudFormationTemplate template = new MySqlCloudFormationTemplate();
        template.provisionStack();

        // when

        // then
    }
}
