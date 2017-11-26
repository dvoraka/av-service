package dvoraka.avservice.runner;

import dvoraka.avservice.runner.server.amqp.AmqpFileServerRunner;
import dvoraka.avservice.runner.server.jms.JmsFileServerRunner;

public interface RunnerConfigurationHelper {

    default RunnerConfiguration jmsFileServerConfiguration() {

        JmsFileServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "jmsFileServerRunner",
                new JmsFileServerRunner(),
                null
        );
    }

    default RunnerConfiguration amqpFileServerConfiguration() {

        AmqpFileServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "amqpFileServerRunner",
                new AmqpFileServerRunner(),
                null
        );
    }
}
