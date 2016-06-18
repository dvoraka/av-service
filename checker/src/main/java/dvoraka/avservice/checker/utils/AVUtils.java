package dvoraka.avservice.checker.utils;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.exception.UnknownProtocolException;
import dvoraka.avservice.checker.receiver.Receiver;
import dvoraka.avservice.checker.sender.Sender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ConnectException;

/**
 * AVUtils.
 */
public class AVUtils implements AmqpUtils {

    @Autowired
    private Sender sender;
    @Autowired
    private Receiver receiver;

    private static Logger logger = LogManager.getLogger();

    private boolean senderOutput;
    private boolean receiverOutput;

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

    // TODO: redesign
//    /**
//     * Tries concrete protocol version.
//     *
//     * @param protocolVersion the protocol version
//     * @return result
//     */
//    public boolean tryProtocol(String protocolVersion) {
//        String result = findProtocol(new String[]{protocolVersion});
//
//        return !(result == null);
//    }

//    /**
//     * Finds first possible protocol version. Starts from the protocols array
//     * end.
//     *
//     * @param protocols the protocols array
//     * @return the protocol version
//     */
    // TODO: redesign
//    public String findProtocol(String[] protocols) {
//        disableOutputFlags();
//
//        String procotolVersion = null;
//        try {
//            procotolVersion = negotiateProtocol(protocols);
//        } catch (UnknownProtocolException e) {
//            // e.printStackTrace();
//        }
//
//        resetOutputFlags();
//
//        return procotolVersion;
//    }

    @Override
    public String negotiateProtocol(String[] protocols)
            throws UnknownProtocolException {

        for (String protocol : protocols) {
            sender.setProtocolVersion(protocol);

            try {
                receiver.receive(sender.sendFile(false, "antivirus"));
                return protocol;
            } catch (ConnectException e) {
                logger.debug(e);
                throw new UnknownProtocolException();
            } catch (InterruptedException e) {
                logger.warn(e);
                Thread.currentThread().interrupt();
            } catch (ProtocolException e) {
                logger.info(e);
            } catch (LastMessageException e) {
                logger.debug(e);
            } catch (IOException e) {
                logger.warn("negotiation failed", e);
                throw new UnknownProtocolException();
            }
        }

        return null;
    }
}
