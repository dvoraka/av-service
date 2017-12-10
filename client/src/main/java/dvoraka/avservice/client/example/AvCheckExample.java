package dvoraka.avservice.client.example;

import dvoraka.avservice.client.AvMessageFuture;
import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.util.Utils;
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

        // generate message
        AvMessage avMessage = Utils.genMessage();

        // send it and get response
        AvMessage response;
        try {
            AvMessageFuture futureResponse = avServiceClient.checkMessage(avMessage);
            response = futureResponse.get();
        } finally {
            context.close();
        }

        // raw output
        System.out.println("Response: " + response);
        // virus info
        System.out.println("Virus info: " + (response != null ? response.getVirusInfo() : ""));
    }
}
