package dvoraka.avservice.server;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ProcessedAVMessageListener;
import dvoraka.avservice.common.AVMessageListener;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.configuration.AppConfig;
import dvoraka.avservice.service.ServiceManagement;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * AMQP AV server implementation
 */
public class BasicAvServer implements AVServer, ServiceManagement, AVMessageListener, ProcessedAVMessageListener {

    @Autowired
    private AVMessageReceiver avMessageReceiver;
    @Autowired
    private MessageProcessor messageProcessor;

    private boolean started;
    private boolean stopped;
    private boolean running;


    public static void main(String[] args) throws InterruptedException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp");
        context.register(AppConfig.class);
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
        avMessageReceiver.addAVMessageListener(this);
        messageProcessor.addProcessedAVMessageListener(this);
        setRunning(true);
    }

    @Override
    public void stop() {
        setStopped(true);
        avMessageReceiver.removeAVMessageListener(this);
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

    @Override
    public void setRunning(boolean value) {
        this.running = value;
    }

    @Override
    public void onAVMessage(AVMessage message) {
        System.out.println("AVMessage: " + message);
    }

    @Override
    public void onProcessedAVMessage(AVMessage message) {
        System.out.println("Processed AVMessage: " + message);
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
