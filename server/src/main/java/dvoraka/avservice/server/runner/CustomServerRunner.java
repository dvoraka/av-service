package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.ServerConfig;

import java.io.IOException;

/**
 * Custom server runner. You can choose custom configurations and profiles for your use cases.
 */
public class CustomServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        ServiceRunner runner = new CustomServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "server", "client", "amqp", "amqp-server", "no-db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{ServerConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return BasicAvServer.class;
    }
}
