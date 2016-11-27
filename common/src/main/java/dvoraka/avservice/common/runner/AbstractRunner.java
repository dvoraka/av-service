package dvoraka.avservice.common.runner;

import dvoraka.avservice.common.service.ServiceManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Base class for runners.
 */
public abstract class AbstractRunner implements Runner {

    private static boolean testRun;
    private boolean running;
    private volatile boolean stopped;

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected Logger log = LogManager.getLogger(this.getClass().getName());


    /**
     * Returns a configured Spring application context for the runner.
     *
     * @return the application context
     */
    protected AnnotationConfigApplicationContext applicationContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profiles());
        context.register(configClasses());
        context.refresh();

        return context;
    }

    /**
     * Returns Spring profiles in an array.
     *
     * @return the profiles array
     */
    protected String[] profiles() {
        return new String[]{"default"};
    }

    /**
     * Returns all configuration classes for the context in an array.
     *
     * @return the configuration classes array
     */
    protected abstract Class<?>[] configClasses();

    /**
     * Returns a service running class.
     *
     * @return the run class
     */
    protected abstract Class<? extends ServiceManagement> runClass();

    protected String stopMessage() {
        return "Press Enter to stop.";
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
            setRunning(false);
            context.close();
            log.info("Runner stopped.");
        }
    }

    @Override
    public void runAsync() {
        new Thread(this::run).start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        setStopped(true);
    }

    /**
     * Sets a running status.
     *
     * @param running the running flag
     */
    protected void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Waiting for a key after runner is started. You can override it if you don't
     * need any action with empty implementation.
     *
     * @throws IOException if reading from keyboard problem occurs
     */
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
        AbstractRunner.testRun = testRun;
    }
}
