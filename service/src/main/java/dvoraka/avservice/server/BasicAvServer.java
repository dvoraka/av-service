package dvoraka.avservice.server;

import dvoraka.avservice.common.AVMessageListener;
import dvoraka.avservice.common.ProcessedAVMessageListener;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.configuration.AppConfig;
import dvoraka.avservice.service.ServiceManagement;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * AMQP AV server implementation
 */
public class BasicAvServer implements ServiceManagement, AVMessageListener, ProcessedAVMessageListener {

    @Autowired
    private AVMessageReceiver avMessageReceiver;


    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp");
        context.register(AppConfig.class);
        context.refresh();

        SimpleMessageListenerContainer container = context.getBean(SimpleMessageListenerContainer.class);
        container.start();

        context.close();
    }

    @Override
    public void start() {
        avMessageReceiver.addAVMessageListener(this);
    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void onAVMessage(AVMessage message) {
        System.out.println("AVMessage: " + message);
    }

    @Override
    public void onProcessedAVMessage(AVMessage message) {
        System.out.println("Processed AVMessage: " + message);
    }
}
