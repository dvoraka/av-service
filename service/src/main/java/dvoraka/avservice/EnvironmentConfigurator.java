package dvoraka.avservice;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.AVMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.configuration.AppConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Configure environment.
 */
public final class EnvironmentConfigurator {

    private EnvironmentConfigurator() {
    }

    public static void main(String[] args) throws MapperException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp");
        context.register(AppConfig.class);
        context.refresh();

        AVMessage avMessage = Utils.genNormalMessage();
        Message message = AVMessageMapper.transform(avMessage);

        RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
        rabbitTemplate.send(message);

        context.close();
    }
}
