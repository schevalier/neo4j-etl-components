package org.neo4j.integration.platforms;

import java.io.IOException;

import com.amazonaws.util.IOUtils;
import org.stringtemplate.v4.ST;

public class StartupScript
{
    private enum Parameters
    {
        DBRootPassword, DBUser, DBPassword, OnUpgraded
    }

    private final String dbRootPassword;
    private final String dbUser;
    private final String dbPassword;
    private final String onUpgraded;

    public StartupScript( String dbRootPassword,
                          String dbUser,
                          String dbPassword,
                          String onUpgraded )
    {
        this.dbRootPassword = dbRootPassword;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.onUpgraded = onUpgraded;
    }

    public String value() throws IOException
    {
        String script = IOUtils.toString( getClass().getResourceAsStream( "/startup.sh" ) );

        ST template = new ST( script );
        template.add( Parameters.DBRootPassword.name(), dbRootPassword );
        template.add( Parameters.DBUser.name(), dbUser );
        template.add( Parameters.DBPassword.name(), dbPassword );
        template.add( Parameters.OnUpgraded.name(), onUpgraded );

        return template.render();
    }
}
