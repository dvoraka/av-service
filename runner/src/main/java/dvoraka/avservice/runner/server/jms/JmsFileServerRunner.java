package dvoraka.avservice.runner.server.jms;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.ServerConfig;

/**
 * JMS file server runner.
 */
public class JmsFileServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new JmsFileServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "storage", "server", "jms", "db"};
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
