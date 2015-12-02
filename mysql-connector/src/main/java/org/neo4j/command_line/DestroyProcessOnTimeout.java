package org.neo4j.command_line;

import java.util.TimerTask;

class DestroyProcessOnTimeout extends TimerTask
{
    private final Process process;
    private boolean timedOut = false;

    public DestroyProcessOnTimeout( Process process )
    {
        this.process = process;
    }

    @Override
    public void run()
    {
        process.destroy();
        timedOut = true;
    }

    public boolean timedOut()
    {
        return timedOut;
    }
}
