package dvoraka.avservice.runner;

public class Runner {

    private RunnerConfiguration configuration;

    private String name;
    private RunningState state;


    public Runner(RunnerConfiguration configuration) {
        this.configuration = configuration;

        this.name = configuration.getName();

        this.state = RunningState.NEW;
    }

    public RunnerConfiguration getConfiguration() {
        return configuration;
    }

    public String getName() {
        return name;
    }

    public RunningState getState() {
        return state;
    }

    public void setState(RunningState state) {
        this.state = state;
    }

    public void start() {
        getConfiguration().getServiceRunner().runAsync();
        setState(RunningState.STARTING);
    }

    public void stop() {
        getConfiguration().getServiceRunner().stop();
        setState(RunningState.STOPPED);
    }

    public boolean isRunning() {
        return getConfiguration().running().getAsBoolean();
    }
}
