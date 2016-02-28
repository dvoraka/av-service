package dvoraka.avservice.checker;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.exception.UnknownProtocolException;
import dvoraka.avservice.checker.receiver.AVReceiver;
import dvoraka.avservice.checker.receiver.Receiver;
import dvoraka.avservice.checker.sender.AVSender;
import dvoraka.avservice.checker.sender.Sender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Created by dvoraka on 23.4.14.
 */
public class AVUtils implements AmqpUtils {

    private static Logger logger = LogManager.getLogger();

    private Sender sender;
    private Receiver receiver;

    private boolean senderOutput;
    private boolean receiverOutput;

    public AVUtils() {
        this(null, null);
    }

    public AVUtils(Sender sender, Receiver receiver) {
        if (sender == null) {
            this.sender = new AVSender("localhost", false);
        } else {
            this.sender = sender;
        }

        if (receiver == null) {
            this.receiver = new AVReceiver("localhost", false);
        } else {
            this.receiver = receiver;
        }
    }

    /**
     * Sets new output flags and saves old ones.
     */
    private void disableOutputFlags() {
        senderOutput = sender.getVerboseOutput();
        sender.setVerboseOutput(false);

        receiverOutput = receiver.getVerboseOutput();
        receiver.setVerboseOutput(false);
    }

    /**
     * Restores old output flags.
     */
    private void resetOutputFlags() {
        sender.setVerboseOutput(senderOutput);
        receiver.setVerboseOutput(receiverOutput);
    }

    /**
     * Tries concrete protocol version.
     *
     * @param protocolVersion the protocol version
     * @return result
     */
    public boolean tryProtocol(String protocolVersion) {
        String result = findProtocol(new String[]{protocolVersion});

        return !(result == null);
    }

    /**
     * Finds first possible protocol version. Starts from the protocols array
     * end.
     *
     * @param protocols the protocols array
     * @return the protocol version
     */
    public String findProtocol(String[] protocols) {
        disableOutputFlags();

        String procotolVersion = null;
        try {
            procotolVersion = negotiateProtocol(protocols);
        } catch (UnknownProtocolException e) {
            // e.printStackTrace();
        }

        resetOutputFlags();

        return procotolVersion;
    }

    @Override
    public String negotiateProtocol(String[] protocols)
            throws UnknownProtocolException {

        String protocol;
        int loop = 0;
        int maxLoops = 20;
        for (int i = protocols.length; i > 0; i--) {
            protocol = protocols[i - 1];
            // System.out.println("Trying " + protocol + "...");
            sender.setProtocolVersion(protocol);

            try {
                receiver.receive(sender.sendFile(false, "antivirus"));
                return protocol;
            } catch (ConnectException e) {
                throw new UnknownProtocolException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                // e.printStackTrace();
            } catch (LastMessageException e) {
                // try again
                i++;
                e.printStackTrace();
            } catch (IOException e) {
                logger.warn("negotiation failed", e);
                throw new UnknownProtocolException();
            }

            if (loop > maxLoops) {
                break;
            }

            loop++;
        }

        return null;
    }
}
