package dvoraka.avservice.server;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ProcessedAVMessageListener;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.service.ServiceManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * AMQP AV server implementation
 */
public class BasicAvServer implements AvServer, ServiceManagement, AvMessageListener, ProcessedAVMessageListener {

    @Autowired
    private ServerComponent serverComponent;
    @Autowired
    private MessageProcessor messageProcessor;

    private static final Logger log = LogManager.getLogger(BasicAvServer.class.getName());

    private boolean started;
    private boolean stopped = true;
    private boolean running;


    public static void main(String[] args) throws InterruptedException {
        // TODO: fix
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp", "amqp-async");
        context.register(ServiceConfig.class);
        context.refresh();

        SimpleMessageListenerContainer container = context.getBean(SimpleMessageListenerContainer.class);
        container.start();

        BasicAvServer server = context.getBean(BasicAvServer.class);
        server.start();

        final long runTime = 10;
        TimeUnit.MINUTES.sleep(runTime);

        context.close();
    }

    @Override
    public void start() {
        setStarted(true);
        serverComponent.addAVMessageListener(this);
        messageProcessor.addProcessedAVMessageListener(this);
        setRunning(true);
    }

    @Override
    public void stop() {
        setStopped(true);
        serverComponent.removeAVMessageListener(this);
        setRunning(false);
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean value) {
        this.running = value;
    }

    @Override
    public void onAVMessage(AvMessage message) {
        messageProcessor.sendMessage(message);
    }

    @Override
    public void onProcessedAVMessage(AvMessage message) {
        serverComponent.sendMessage(message);
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
