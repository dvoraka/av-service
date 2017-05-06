package dvoraka.avservice.common.runner;

import dvoraka.avservice.common.service.ApplicationManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Base class for application runners.
 */
public abstract class AbstractAppRunner implements AppRunner {

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected Logger log = LogManager.getLogger(this.getClass().getName());

    private boolean running;


    /**
     * Returns a configured Spring application context for the runner.
     *
     * @return the application context
     */
    protected AnnotationConfigApplicationContext applicationContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profiles());

        // improve Spring context with itest profile if system property exists
        if (System.getProperty("itest") != null) {
            context.getEnvironment().addActiveProfile("itest");
        }

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
    protected abstract Class<? extends ApplicationManagement> runClass();

    /**
     * Runs the runner.
     */
    @Override
    public void run() {
        AnnotationConfigApplicationContext context = applicationContext();
        ApplicationManagement application = context.getBean(runClass());

        setRunning(true);
        log.info("Runner started.");
        application.start();

        setRunning(false);
        log.info("Runner stopped.");
        context.close();
    }

    @Override
    public void runAsync() {
        new Thread(this::run).start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets a running status.
     *
     * @param running the running flag
     */
    protected void setRunning(boolean running) {
        this.running = running;
    }
}
