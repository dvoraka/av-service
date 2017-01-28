package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Processor for composition of processors.
 */
public class CompositeMessageProcessor implements MessageProcessor, AvMessageListener {

    private final List<ProcessorConfiguration> processors;
    private final List<AvMessageListener> listeners;

    private final BlockingQueue<AvMessage> queue;


    public CompositeMessageProcessor() {
        processors = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        queue = new SynchronousQueue<>();
    }

    @Override
    public void sendMessage(AvMessage message) {

        AvMessage lastResult = message;
        for (ProcessorConfiguration processor : processors) {
            // check input condition
            final AvMessage temp = lastResult;
            System.out.println("Condition check for: " + temp);
            if (!processor.getInputCondition()
                    .map(predicate -> predicate.test(temp))
                    .orElse(true)) {

                System.out.println("Aborting...");
                notifyListeners(listeners, temp.createErrorResponse("Input condition failed!"));
                break;
            }

            // process message in a new thread
            final AvMessage data;
            if (processor.isUseOriginalMessage()) {
                data = message;
            } else {
                data = lastResult;
            }
            System.out.println("Sending: " + message);
            new Thread(() -> processor.getProcessor().sendMessage(data))
                    .start();

            // wait for result
            try {
                final int waitTime = 5;
                AvMessage result = queue.poll(waitTime, TimeUnit.SECONDS);
                System.out.println("Result: " + result);
                lastResult = result;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
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
        listeners.add(listener);
    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
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
