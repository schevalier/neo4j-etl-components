package org.neo4j.etl.process;

import java.util.Timer;
import java.util.TimerTask;

class DestroyProcessOnTimeout extends TimerTask
{
    private final Process process;
    private final Timer timer;
    private boolean timedOut = false;

    public DestroyProcessOnTimeout( Process process, Timer timer )
    {
        this.process = process;
        this.timer = timer;
    }

    @Override
    public void run()
    {
        process.destroy();
        timer.cancel();

        timedOut = true;
    }

    public boolean timedOut()
    {
        return timedOut;
    }
}
