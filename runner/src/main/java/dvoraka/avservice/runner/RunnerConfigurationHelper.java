package dvoraka.avservice.runner;

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
}
