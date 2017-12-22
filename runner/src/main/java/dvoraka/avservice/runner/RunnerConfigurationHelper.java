package dvoraka.avservice.runner;

import dvoraka.avservice.runner.runnerconfiguration.DefaultRunnerConfiguration;
import dvoraka.avservice.runner.runnerconfiguration.RunnerConfiguration;
import dvoraka.avservice.runner.server.amqp.AmqpCheckServerRunner;
import dvoraka.avservice.runner.server.amqp.AmqpFileServerRunner;
import dvoraka.avservice.runner.server.jms.JmsCheckServerRunner;
import dvoraka.avservice.runner.server.jms.JmsFileServerRunner;
import dvoraka.avservice.runner.server.kafka.KafkaCheckServerRunner;
import dvoraka.avservice.runner.server.kafka.KafkaFileServerRunner;

public interface RunnerConfigurationHelper {

    default RunnerConfiguration amqpFileServerConfiguration() {

        AmqpFileServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "amqpFileServerRunner",
                new AmqpFileServerRunner(),
                null
        );
    }

    default RunnerConfiguration jmsFileServerConfiguration() {

        JmsFileServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "jmsFileServerRunner",
                new JmsFileServerRunner(),
                null
        );
    }

    default RunnerConfiguration kafkaFileServerConfiguration() {

        KafkaFileServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "kafkaFileServerRunner",
                new KafkaFileServerRunner(),
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

    default RunnerConfiguration jmsCheckServerConfiguration() {

        JmsCheckServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "jmsCheckServerRunner",
                new JmsCheckServerRunner(),
                null
        );
    }

    default RunnerConfiguration kafkaCheckServerConfiguration() {

        KafkaCheckServerRunner.setTestRun(false);

        return new DefaultRunnerConfiguration(
                "kafkaCheckServerRunner",
                new KafkaCheckServerRunner(),
                null
        );
    }
}
