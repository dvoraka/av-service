package dvoraka.avservice.checker.utils;

import dvoraka.avservice.common.exception.LastMessageException;
import dvoraka.avservice.common.exception.ProtocolException;
import dvoraka.avservice.common.exception.UnknownProtocolException;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.AvSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Anti-virus utils.
 */
public class AvUtils implements AmqpUtils {

    @Autowired
    private AvSender avSender;
    @Autowired
    private AvReceiver avReceiver;

    private static final Logger log = LogManager.getLogger();

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

    /**
     * Tries the concrete protocol version.
     *
     * @param protocolVersion the protocol version
     * @return true if the protocol works
     */
    public boolean tryProtocolVersion(String protocolVersion) {
        String result = findProtocolVersion(new String[]{protocolVersion});

        return !(result == null);
    }

    /**
     * Finds the first possible protocol version.
     *
     * @param protocols the protocols array
     * @return the protocol version
     */
    public String findProtocolVersion(String[] protocols) {
        disableOutputFlags();

        String procotolVersion = null;
        try {
            procotolVersion = negotiateProtocol(protocols);
        } catch (UnknownProtocolException e) {
            log.debug("Protocol version not found.", e);
        }

        resetOutputFlags();

        return procotolVersion;
    }

    @Override
    public String negotiateProtocol(String[] protocols) throws UnknownProtocolException {
        for (String protocol : protocols) {
            avSender.setProtocolVersion(protocol);
            try {
                avReceiver.receive(avSender.sendFile(false, "antivirus"));
                return protocol;
            } catch (ConnectException e) {
                log.debug(e);
                throw new UnknownProtocolException("Unknown protocol: " + protocol);
            } catch (InterruptedException e) {
                log.warn(e);
                Thread.currentThread().interrupt();
            } catch (ProtocolException e) {
                log.info(e);
            } catch (LastMessageException e) {
                log.debug(e);
            } catch (IOException e) {
                log.warn("negotiation failed", e);
                throw new UnknownProtocolException("Unknown protocol: " + protocol);
            }
        }

        return null;
    }
}
