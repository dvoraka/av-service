package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Configure environment.
 */
public final class EnvironmentConfigurator {

    private EnvironmentConfigurator() {
    }

    public static void main(String[] args) throws MapperException {
        // create AMQP queues, exchanges and bindings
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("core", "amqp", "amqp-server", "no-db");
        context.register(AmqpConfig.class);
        context.refresh();
        context.close();
    }
}
