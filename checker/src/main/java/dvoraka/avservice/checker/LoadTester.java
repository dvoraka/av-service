package dvoraka.avservice.checker;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.MaxLoopsReachedException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.receiver.AVReceiver;
import dvoraka.avservice.checker.receiver.Receiver;
import dvoraka.avservice.checker.sender.AVSender;
import dvoraka.avservice.checker.sender.Sender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * AMQP anti-virus load tester.
 * <p>
 * Created by dvoraka on 17.4.14.
 */
// TODO: Spring
public class LoadTester implements Tester {

    private static Logger logger = LogManager.getLogger();

    private static final int MAX_MSG_EXCEPTIONS = 3;
    private static final int MAX_LOOPS = 10;
    private static final int MS_PER_SECOND = 1000;

    private LoadTestProperties props;
    private Sender sender;
    private Receiver receiver;

    private int maxLoops;
    private int maxMsgExceptions;


    public LoadTester() {
        this(null, null, null);
    }

    public LoadTester(LoadTestProperties props) {
        this(props, null, null);
    }

    public LoadTester(LoadTestProperties props, Sender sender, Receiver receiver) {
        if (props == null) {
            this.props = new BasicProperties();
        } else {
            this.props = props;
        }

        if (sender == null) {
            this.sender = new AVSender(this.props.getHost(), false);
        } else {
            this.sender = sender;
        }

        if (receiver == null) {
            this.receiver = new AVReceiver(this.props.getHost(), false);
        } else {
            this.receiver = receiver;
        }

        this.maxLoops = MAX_LOOPS;
        this.maxMsgExceptions = MAX_MSG_EXCEPTIONS;
    }

    public static void main(String[] args) throws IOException {
        LoadTestProperties ltp = new BasicProperties();
        Tester lt = new LoadTester(ltp);
        lt.startTest();
    }

    public Collection<String> sendMessages() throws IOException {
        Collection<String> messageIDs = new HashSet<>(getProps().getMsgCount() / 2);

        for (int i = 0; i < getProps().getMsgCount(); i++) {
            messageIDs.add(getSender().sendFile(true, getProps().getAppId()));
        }

        return messageIDs;
    }

    public void receiveMessages(Collection<String> messageIDs) throws IOException, MaxLoopsReachedException {

        int loopCounter = 1;
        int exceptionCounter = 0;
        while (!messageIDs.isEmpty()) {
            if (loopCounter > maxLoops) {
                throw new MaxLoopsReachedException();
            }

            exceptionCounter = 0;
            Iterator<String> it = messageIDs.iterator();
            while (it.hasNext()) {
                try {
                    String item = it.next();
                    boolean virus = getReceiver().receive(item);
                    it.remove();
                } catch (InterruptedException e) {
                    logger.warn("Receiving interrupted!");
                    Thread.currentThread().interrupt();
                } catch (ProtocolException e) {
                    logger.info(e);
                } catch (LastMessageException e) {
                    logger.warn("", e);
                    if (exceptionCounter < this.maxMsgExceptions) {
                        exceptionCounter++;
                    } else {
                        exceptionCounter = 0;
                        break;
                    }
                }
            }
            logger.debug(loopCounter + ". loop");

            try {
                final long sleepTime = 1000;
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.warn("Sleeping interrupted!");
                Thread.currentThread().interrupt();
            }

            loopCounter++;
        }
    }

    @Override
    public void startTest() throws IOException {
        System.out.println("Load test start for " + getProps().getMsgCount() + " messages...");
        long begin = System.currentTimeMillis();

        // purge queue before test start
        getSender().purgeQueue(getProps().getDestinationQueue());

        if (getProps().isSynchronous()) {
            synchronousTest();
        } else {
            asynchronousTest(begin);
        }

        printTestingTime(begin);
    }

    private void printTestingTime(long begin) {
        long duration = System.currentTimeMillis() - begin;
        System.out.println("");
        System.out.println("Load test end");
        System.out.println("Duration: " + (duration / MS_PER_SECOND) + " s");
        System.out.println("Messages: " + getProps().getMsgCount() / (duration / MS_PER_SECOND) + "/s");
    }

    private void synchronousTest() throws IOException {
        System.out.println("Sending and receiving...");

        Collection<String> skippedMessages = new HashSet<>();
        String msgId = null;
        for (int i = 0; i < getProps().getMsgCount(); i++) {
            try {
                msgId = getSender().sendFile(true, getProps().getAppId());
                getReceiver().receive(msgId);
            } catch (InterruptedException e) {
                logger.warn("Receiving interrupted!");
                Thread.currentThread().interrupt();
            } catch (ProtocolException e) {
                logger.info(e);
            } catch (LastMessageException e) {
                skippedMessages.add(msgId);
                logger.debug("receiving failed", e);
            }
        }

        int loopCounter = 0;
        while (!skippedMessages.isEmpty()) {
            if (loopCounter > maxLoops) {
                try {
                    throw new MaxLoopsReachedException();
                } catch (MaxLoopsReachedException e) {
                    logger.warn("unreceivable messages", e);
                    break;
                }
            }

            Iterator<String> it = skippedMessages.iterator();
            while (it.hasNext()) {
                try {
                    getReceiver().receive(it.next());
                    it.remove();
                } catch (InterruptedException e) {
                    logger.warn("Receiving interrupted!");
                    Thread.currentThread().interrupt();
                } catch (ProtocolException e) {
                    logger.info(e);
                } catch (LastMessageException e) {
                    logger.debug("receiving failed", e);
                }
            }

            loopCounter++;
        }

        System.out.println("Done");
    }

    private void asynchronousTest(long begin) throws IOException {
        System.out.println("Sending messages...");
        Collection<String> messageIDs = sendMessages();

        printSendingTime(begin);

        if (!getProps().isSendOnly()) {
            // receive messages
            System.out.println("Receiving responses...");

            try {
                receiveMessages(messageIDs);
            } catch (MaxLoopsReachedException e) {
                logger.warn("receiving messages failed", e);
            }

            System.out.println("Receiving end");
        }
    }

    private void printSendingTime(long begin) {
        long afterSend = System.currentTimeMillis();
        System.out.println("All messages sent");
        System.out.println("Time: " + ((afterSend - begin) / MS_PER_SECOND) + " s");
        System.out.println("");
    }

    public LoadTestProperties getProps() {
        return props;
    }

    public void setProps(LoadTestProperties props) {
        this.props = props;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
}
