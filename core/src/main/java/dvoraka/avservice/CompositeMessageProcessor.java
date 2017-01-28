package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Processor for composition of processors.
 */
public class CompositeMessageProcessor implements MessageProcessor, AvMessageListener {

    private final List<ProcessorConfiguration> processors;

    private final BlockingQueue<AvMessage> queue;


    public CompositeMessageProcessor() {
        this.processors = new ArrayList<>();
        queue = new SynchronousQueue<>();
    }

    @Override
    public void sendMessage(AvMessage message) {

        AvMessage lastResult = message;
        for (ProcessorConfiguration processor : processors) {

            // check input condition
            Optional<Predicate<? super AvMessage>> condition = processor.getInputCondition();

            AvMessage temp = lastResult;
            if (!condition.map(predicate -> predicate.test(temp))
                    .orElse(true)) {

                System.out.println("Aborting...");
                break;
            }

            // process message
            System.out.println("Sending: " + message);
            new Thread(() -> processor.getProcessor()
                    .sendMessage(message))
                    .start();

            // wait for result
            try {
                final int waitTime = 5;
                AvMessage result = queue.poll(waitTime, TimeUnit.SECONDS);
                System.out.println("Result: " + result);
                lastResult = result;

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
                configuration -> configuration.getProcessor()
                        .addProcessedAVMessageListener(this));
        processors.forEach(
                configuration -> configuration.getProcessor()
                        .start());
    }

    @Override
    public void stop() {
        processors.forEach(
                configuration -> configuration.getProcessor()
                        .stop());
    }

    @Override
    public void addProcessedAVMessageListener(AvMessageListener listener) {
    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {

    }

    public void addProcessor(ProcessorConfiguration configuration) {
        processors.add(configuration);
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
