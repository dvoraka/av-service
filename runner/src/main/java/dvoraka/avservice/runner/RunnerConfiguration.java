package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

/**
 * Runner configuration.
 */
public interface RunnerConfiguration {

    String getId();

    ServiceRunner getServiceRunner();
}
