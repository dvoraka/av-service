package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.rest.service.RemoteRestService;
import dvoraka.avservice.rest.service.RestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * Remote REST configuration for the import.
 */
//TODO: merge profiles to 'rest-remote'
@Profile({"rest-amqp", "rest-jms"})
public class RemoteRestConfig {

    @Bean
    public RestService avRestService(
            AvServiceClient avServiceClient,
            FileServiceClient fileServiceClient,
            ResponseClient responseClient
    ) {
        return new RemoteRestService(avServiceClient, fileServiceClient, responseClient);
    }
}
