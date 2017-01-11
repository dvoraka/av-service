package dvoraka.avservice.common.runner;

import dvoraka.avservice.common.service.ServiceManagement;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Base class for service runners.
 */
public abstract class AbstractServiceRunner extends AbstractAppRunner implements ServiceRunner {

    private static boolean testRun;
    private volatile boolean stopped;


    /**
     * Returns a service running class.
     *
     * @return the run class
     */
    protected abstract Class<? extends ServiceManagement> runClass();

    protected String stopMessage() {
        return "Press Enter to stop."; //NOSONAR
    }

    /**
     * Runs the runner.
     */
    @Override
    public void run() {
        AnnotationConfigApplicationContext context = applicationContext();
        ServiceManagement service = context.getBean(runClass());
        service.start();

        setRunning(true);

        log.info("Runner started.");
        try {
            waitForKey();
        } catch (IOException e) {
            log.error("Runner problem!", e);
        } finally {
            service.stop();
            stop();
            setRunning(false);
            context.close();
            log.info("Runner stopped.");
        }
    }

    @Override
    public void stop() {
        setStopped(true);
    }

    /**
     * Waiting for a key after runner is started. You can override it if you don't
     * need any action with empty implementation.
     *
     * @throws IOException if reading from keyboard problem occurs
     */
    // TODO: improve
    protected void waitForKey() throws IOException {
        if (testRun) {
            return;
        }

        System.out.println(stopMessage());

        while (true) {
            if (isStopped()) {
                break;
            }

            if (System.in.available() == 0) {
                final long sleepTime = 1_000;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.warn("Sleeping interrupted!", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {
                break;
            }
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    /**
     * Special testing flag for not waiting on keyboard after start.
     *
     * @return if the run is only for testing
     */
    public static boolean isTestRun() {
        return testRun;
    }

    /**
     * Setting a testing flag.
     *
     * @param testRun the testing flag
     */
    public static void setTestRun(boolean testRun) {
        AbstractServiceRunner.testRun = testRun;
    }
}
