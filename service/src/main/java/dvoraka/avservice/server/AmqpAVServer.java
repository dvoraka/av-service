package dvoraka.avservice.server;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.CustomThreadFactory;
import dvoraka.avservice.ProcessedAVMessageListener;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.AVMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.configuration.AppConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * AMQP AV server.
 */
public class AmqpAVServer extends AbstractAVServer implements AVServer, ProcessedAVMessageListener {

    @Autowired
    private MessageProcessor messageProcessor;
    @Autowired
    private ListeningStrategy listeningStrategy;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger log = LogManager.getLogger(AmqpAVServer.class.getName());

    private static final long POOL_TERM_TIME_S = 10;
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

        final long runTime = 6_000_000;
        // TODO: create management interface
        try {
            Thread.sleep(runTime);
        } catch (InterruptedException e) {
            log.debug("Running interrupted!", e);
            Thread.currentThread().interrupt();
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
                final long sleepTime = 100;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.warn("Sleeping interrupted", e);
                    Thread.currentThread().interrupt();
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
    public void onProcessedAVMessage(AVMessage message) {
        processResponse(message);
    }

    @Override
    public void start() {
        log.debug("Server start.");
        setStarted();

        if (getReceivingType() == ReceivingType.POLLING) {
            startResponding();
        } else if (getReceivingType() == ReceivingType.LISTENER) {
            messageProcessor.addProcessedAVMessageListener(this);
        }

        startListening();
        setRunning(true);
    }

    @PreDestroy
    @Override
    public void stop() {
        if (isRunning()) {
            log.debug("Server stop.");
            setStopped();

            listeningStrategy.stop();
            messageProcessor.stop();
            setRunning(false);
        } else {
            log.debug("Server is not running.");
        }

        if (!executorService.isTerminated()) {

            executorService.shutdown();
            try {
                executorService.awaitTermination(POOL_TERM_TIME_S, TimeUnit.SECONDS);
                log.debug("Server pool stopped.");
            } catch (InterruptedException e) {
                log.warn("Pool shutdown interrupted!", e);
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void restart() {

    }

    public ReceivingType getReceivingType() {
        return receivingType;
    }

    protected void setMessageProcessor(MessageProcessor processor) {
        messageProcessor = processor;
    }

    protected void setListeningStrategy(ListeningStrategy strategy) {
        listeningStrategy = strategy;
    }
}
