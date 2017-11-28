package dvoraka.avservice.runner;

import dvoraka.avservice.runner.runnerconfiguration.DefaultRunnerConfiguration;
import dvoraka.avservice.runner.runnerconfiguration.RunnerConfiguration;
import dvoraka.avservice.runner.server.amqp.AmqpCheckServerRunner;
import dvoraka.avservice.runner.server.amqp.AmqpFileServerRunner;
import dvoraka.avservice.runner.server.jms.JmsCheckServerRunner;
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

    default RunnerConfiguration jmsCheckServerConfiguration() {

        JmsCheckServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "jmsCheckServerRunner",
                new JmsCheckServerRunner(),
                null
        );
    }

    default RunnerConfiguration amqpCheckServerConfiguration() {

        AmqpCheckServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "amqpCheckServerRunner",
                new AmqpCheckServerRunner(),
                null
        );
    }
}
