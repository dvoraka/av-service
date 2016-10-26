package dvoraka.avservice.server;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AMQP AV server implementation
 */
public class BasicAvServer implements AvServer {

    @Autowired
    private ServerComponent serverComponent;
    @Autowired
    private MessageProcessor messageProcessor;
    @Autowired
    private MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(BasicAvServer.class.getName());
    public static final AvMessageSource MESSAGE_SOURCE = AvMessageSource.SERVER;

    private boolean started;
    private boolean stopped = true;
    private boolean running;
    private String serviceId;


    public BasicAvServer(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public void start() {
        log.debug("Server started.");
        setStopped(false);
        setStarted(true);

        serverComponent.addAvMessageListener(this);
        messageProcessor.addProcessedAVMessageListener(this);

        setRunning(true);
        log.debug("Server is running.");
    }

    @Override
    public void stop() {
        log.debug("Server stopped.");
        setStopped(true);
        serverComponent.removeAvMessageListener(this);
        setRunning(false);

        log.debug("Server has stopped");
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
    public void onAvMessage(AvMessage message) {
        messageInfoService.save(message, MESSAGE_SOURCE, serviceId);
        messageProcessor.sendMessage(message);
    }

    @Override
    public void onProcessedAvMessage(AvMessage message) {
        serverComponent.sendMessage(message);
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
