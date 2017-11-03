package dvoraka.avservice.runner;

public class Runner {

    private RunnerConfiguration configuration;


    public Runner(RunnerConfiguration configuration) {
        this.configuration = configuration;
    }

    public RunnerConfiguration getConfiguration() {
        return configuration;
    }
}
