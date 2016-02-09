package dvoraka.avservice;

/**
 * AMQP AV server.
 */
public class AmqpAVServer implements AVServer {

    private boolean started;
    private boolean stopped;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
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
    public void restart() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
