package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;

import java.io.IOException;

/**
 * File server runner.
 */
public class AmqpFileServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        AmqpFileServerRunner runner = new AmqpFileServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "amqp", "amqp-file-server", "db"};
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
