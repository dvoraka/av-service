package dvoraka.avservice.server;

/**
 * Abstract AV server.
 */
public abstract class AbstractAVServer implements AVServer {

    private boolean started;
    private boolean stopped;
    private boolean running;

    @Override
    public abstract void start();

    public void setStarted() {
        started = true;
    }

    @Override
    public abstract void stop();

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
    public abstract void restart();

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }
    @Override
    public boolean isRunning() {
        return running;
    }
}
