package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Processor for composition of processors.
 */
public class CompositeMessageProcessor implements MessageProcessor, AvMessageListener {

    private final List<MessageProcessor> processors;

    private final BlockingQueue<AvMessage> queue;


    public CompositeMessageProcessor() {
        this.processors = new ArrayList<>();
        queue = new SynchronousQueue<>();
    }

    @Override
    public void sendMessage(AvMessage message) {
        for (MessageProcessor processor : processors) {
            new Thread(() -> processor.sendMessage(message)).start();
            try {
                final int waitTime = 5;
                System.out.println("Result: " + queue.poll(waitTime, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return null;
    }

    @Override
    public void start() {
        processors.forEach(
                processor -> processor.addProcessedAVMessageListener(this));
        processors.forEach(
                MessageProcessor::start);
    }

    @Override
    public void stop() {
        processors.forEach(MessageProcessor::stop);
    }

    @Override
    public void addProcessedAVMessageListener(AvMessageListener listener) {
    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {

    }

    public void addProcessor(MessageProcessor processor) {
        processors.add(processor);
    }

    @Override
    public void onAvMessage(AvMessage message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
