package dvoraka.avservice.server;

/**
 * Abstract AV server.
 *
 * @deprecated Concept which will be removed.
 */
@Deprecated
public abstract class AbstractAvServer implements AvServer {

    private boolean started;
    private boolean stopped;
    private boolean running;


    public void setStarted() {
        started = true;
    }

    public void setStopped() {
        stopped = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }
}
