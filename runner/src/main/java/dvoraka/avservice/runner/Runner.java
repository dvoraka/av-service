package dvoraka.avservice.runner;

public class Runner {

    private RunnerConfiguration configuration;

    private long id;
    private String name;
    private RunningState state;


    public Runner(RunnerConfiguration configuration, long id) {
        this.configuration = configuration;

        this.id = id;
        this.name = configuration.getName();

        this.state = RunningState.NEW;
    }

    public RunnerConfiguration getConfiguration() {
        return configuration;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RunningState getState() {
        return state;
    }

    public void start() {
        getConfiguration().getServiceRunner().runAsync();
    }

    public boolean isRunning() {
        return getConfiguration().running().getAsBoolean();
    }
}
