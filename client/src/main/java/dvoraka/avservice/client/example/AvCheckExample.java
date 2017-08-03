package dvoraka.avservice.client.example;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.response.AvMessageFuture;
import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Anti-virus checking example.
 */
public final class AvCheckExample {

    private AvCheckExample() {
    }

    public static void main(String[] args) throws InterruptedException {
        // initialize client context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("client", "amqp", "file-client", "no-db");
        context.register(ClientConfig.class);
        context.refresh();

        // get client
        AvServiceClient avServiceClient = context.getBean(AvServiceClient.class);

        // generate message and send it
        AvMessage avMessage = Utils.genMessage();
        AvMessageFuture futureResponse = avServiceClient.checkMessage(avMessage);

        // get response
        AvMessage response = futureResponse.get();
        // raw output
        System.out.println("Response: " + response);
        // virus info
        System.out.println("Virus info: " + (response != null ? response.getVirusInfo() : ""));

        context.close();
    }
}
