package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.service.ServiceManagement;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Base class for runners.
 */
public abstract class AbstractRunner {

    private static boolean testRun;


    public AnnotationConfigApplicationContext applicationContext() {
        return new AnnotationConfigApplicationContext();
    }

    public String[] profiles() {
        return new String[]{"default"};
    }

    public abstract Class<?>[] configClasses();

    public abstract Class<? extends ServiceManagement> runClass();

    public String message() {
        return "Press Enter to stop.";
    }

    public void run() throws IOException {
        AnnotationConfigApplicationContext context = applicationContext();
        context.getEnvironment().setActiveProfiles(profiles());
        context.register(configClasses());
        context.refresh();

        ServiceManagement service = context.getBean(runClass());
        service.start();

        if (!testRun) {
            System.out.println(message());
            System.in.read();
        }

        context.close();
    }

    public static boolean isTestRun() {
        return testRun;
    }

    public static void setTestRun(boolean testRun) {
        AbstractRunner.testRun = testRun;
    }
}
