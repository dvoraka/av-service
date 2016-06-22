package dvoraka.avservice.checker.utils;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.exception.UnknownProtocolException;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.AvSender;
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
    private AvSender avSender;
    @Autowired
    private AvReceiver avReceiver;

    private static Logger logger = LogManager.getLogger();

    private boolean senderOutput;
    private boolean receiverOutput;

    /**
     * Sets new output flags and saves old ones.
     */
    private void disableOutputFlags() {
        senderOutput = avSender.isVerboseOutput();
        avSender.setVerboseOutput(false);

        receiverOutput = avReceiver.isVerboseOutput();
        avReceiver.setVerboseOutput(false);
    }

    /**
     * Restores old output flags.
     */
    private void resetOutputFlags() {
        avSender.setVerboseOutput(senderOutput);
        avReceiver.setVerboseOutput(receiverOutput);
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
            avSender.setProtocolVersion(protocol);

            try {
                avReceiver.receive(avSender.sendFile(false, "antivirus"));
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
