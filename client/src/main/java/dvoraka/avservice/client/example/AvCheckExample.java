package dvoraka.avservice.client.example;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.response.ResponseClient;
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
        context.getEnvironment().setActiveProfiles(
                "client", "amqp", "file-client", "checker", "no-db"
        );
        context.register(ClientConfig.class);
        context.refresh();

        // get clients
        AvServiceClient avServiceClient = context.getBean(AvServiceClient.class);
        ResponseClient responseClient = context.getBean(ResponseClient.class);

        // generate message and send it
        AvMessage avMessage = Utils.genMessage();
        avServiceClient.checkMessage(avMessage);

        // wait a bit
        final long waitTime = 200;
        Thread.sleep(waitTime);

        // get response
        AvMessage response = responseClient.getResponse(avMessage.getId());
        // raw output
        System.out.println("Response: " + response);
        // virus info
        System.out.println("Virus info: " + response.getVirusInfo());

        context.close();
    }
}
