package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;

import java.io.IOException;

/**
 * Custom server runner. You can choose custom configurations and profiles for your use cases.
 */
public class CustomServerRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        CustomServerRunner runner = new CustomServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"amqp", "no-db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{AmqpConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return BasicAvServer.class;
    }
}
