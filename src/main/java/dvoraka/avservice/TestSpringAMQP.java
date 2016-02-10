package dvoraka.avservice;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Spring AMQP prototype.
 */
public class TestSpringAMQP {

    public static void main(String[] args) {

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        RabbitTemplate template = context.getBean(RabbitTemplate.class);

        template.setReceiveTimeout(1000);
        for (int i = 0; i < 50; i++) {
            Message msg = template.receive();
            if (msg == null) {
                System.out.println("waiting...");
            } else {
                System.out.println(msg);
            }
        }

        context.close();
    }
}
