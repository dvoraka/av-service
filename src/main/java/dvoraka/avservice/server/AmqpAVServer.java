package dvoraka.avservice.server;

import dvoraka.avservice.AVMessageListener;
import dvoraka.avservice.CustomThreadFactory;
import dvoraka.avservice.configuration.AppConfig;
import dvoraka.avservice.MapperException;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.AVMessageMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * AMQP AV server.
 */
public class AmqpAVServer extends AbstractAVServer implements AVServer, AVMessageListener {

    private static final Logger log = LogManager.getLogger(AmqpAVServer.class.getName());

    @Autowired
    private MessageProcessor messageProcessor;
    @Autowired
    private ListeningStrategy listeningStrategy;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String RESPONSE_EXCHANGE = "result";

    private ExecutorService executorService;
    private ReceivingType receivingType;


    public AmqpAVServer(ReceivingType receivingType) {
        ThreadFactory threadFactory = new CustomThreadFactory("server-pool-");
        executorService = Executors.newFixedThreadPool(2, threadFactory);
        this.receivingType = receivingType;
    }

    public static void main(String[] args) {

        // waiting before start
//        try {
//            Thread.sleep(20_000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        AmqpAVServer server = context.getBean(AmqpAVServer.class);

        server.start();

        // TODO: create management interface
        try {
            Thread.sleep(6_000_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();
        context.close();
    }

    private void startListening() {
        Runnable listening = this::listen;
        executorService.execute(listening);
    }

    private void startResponding() {
        Runnable responding = this::response;
        executorService.execute(responding);
    }

    private void listen() {
        log.debug("Starting listening...");
        listeningStrategy.listen();
    }

    private void response() {
        log.debug("Starting responding...");
        while (true) {
            if (isStopped()) {
                break;
            }

            if (messageProcessor.hasProcessedMessage()) {
                AVMessage message = messageProcessor.getProcessedMessage();
                processResponse(message);
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processResponse(AVMessage message) {
        log.debug("Processed message: " + message.getId());

        try {
            Message response = AVMessageMapper.transform(message);
            rabbitTemplate.send(RESPONSE_EXCHANGE, "ROUTINGKEY", response);
        } catch (MapperException e) {
            log.warn("Message problem!", e);
            // TODO: send error response
        }
    }

    @Override
    public void onAVMessage(AVMessage message) {
        processResponse(message);
    }

    @Override
    public void start() {
        log.debug("Server start.");
        setStarted();

        if (getReceivingType() == ReceivingType.POLLING) {
            startResponding();
        } else if (getReceivingType() == ReceivingType.LISTENER) {
            messageProcessor.addAVMessageListener(this);
        }

        startListening();
    }

    @Override
    public void stop() {
        log.debug("Server stop.");
        setStopped();

        listeningStrategy.stop();
        messageProcessor.stop();

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restart() {

    }

    public ReceivingType getReceivingType() {
        return receivingType;
    }
}
