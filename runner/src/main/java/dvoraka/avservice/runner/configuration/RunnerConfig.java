package dvoraka.avservice.runner.configuration;

import dvoraka.avservice.runner.service.DefaultRunnerService;
import dvoraka.avservice.runner.service.RunnerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RunnerConfig {

    @Bean
    public RunnerService runnerService() {
        return new DefaultRunnerService();
    }
}
