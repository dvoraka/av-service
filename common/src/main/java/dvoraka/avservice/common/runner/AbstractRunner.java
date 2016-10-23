package dvoraka.avservice.common.runner;

import dvoraka.avservice.common.service.ServiceManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Base class for runners.
 */
public abstract class AbstractRunner {

    private static boolean testRun;

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected Logger log = LogManager.getLogger(this.getClass().getName());


    protected AnnotationConfigApplicationContext applicationContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profiles());
        context.register(configClasses());
        context.refresh();

        return context;
    }

    protected String[] profiles() {
        return new String[]{"default"};
    }

    protected abstract Class<?>[] configClasses();

    protected abstract Class<? extends ServiceManagement> runClass();

    protected String stopMessage() {
        return "Press Enter to stop.";
    }

    public void run() {
        AnnotationConfigApplicationContext context = applicationContext();
        ServiceManagement service = context.getBean(runClass());
        service.start();

        log.info("Runner started.");
        try {
            waitForKey();
        } catch (IOException e) {
            log.error("Runner problem!", e);
        } finally {
            service.stop();
            context.close();
            log.info("Runner stopped.");
        }
    }

    protected void waitForKey() throws IOException {
        if (!testRun) {
            System.out.println(stopMessage());
            System.in.read();
        }
    }

    public static boolean isTestRun() {
        return testRun;
    }

    public static void setTestRun(boolean testRun) {
        AbstractRunner.testRun = testRun;
    }
}
