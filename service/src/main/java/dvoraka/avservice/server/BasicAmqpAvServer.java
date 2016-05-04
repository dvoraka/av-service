package dvoraka.avservice.server;

import dvoraka.avservice.configuration.AppConfig;
import dvoraka.avservice.service.ServiceManagement;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * AMQP AV server implementation
 */
public class BasicAmqpAvServer implements ServiceManagement, MessageListener {

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

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
}
