package dvoraka.avservice.checker;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.AvSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Diagnostic utility main class.
 *
 * @author dvoraka
 */
public class AVChecker {

    private AvSender sender;
    private AvReceiver receiver;

    private boolean dirtyFile;
    private String appId;

    private static Logger logger = LogManager.getLogger();

    public AVChecker(AvSender sender, AvReceiver receiver) {
        this(sender, receiver, false, "antivirus");
    }

    public AVChecker(AvSender sender, AvReceiver receiver,
                     boolean dirtyFile, String appId) {

        this.sender = sender;
        this.receiver = receiver;

        this.dirtyFile = dirtyFile;
        this.appId = appId;
    }

    public void check() {
        try {
            String messageId = sender.sendFile(isDirtyFile(), getAppId());

            if (isDirtyFile() == receiver.receive(messageId)) {
                // check OK
                System.out.println("Test OK");
            } else {
                System.out.println("Test failed");
                logger.warn("Check problem - bad response");
            }
        } catch (ConnectException e) {
            System.err.println("Connection problem. Is message broker running?");
            logger.warn("Check problem - connection problem", e);
        } catch (IOException | InterruptedException | ProtocolException e) {
            logger.warn("Check problem.", e);
        } catch (LastMessageException e) {
            logger.info(e);
        }
    }

    /**
     * @return the sender
     */
    public AvSender getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(AvSender sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public AvReceiver getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(AvReceiver receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the dirtyFile
     */
    public boolean isDirtyFile() {
        return dirtyFile;
    }

    /**
     * @param dirtyFile the dirtyFile to set
     */
    public void setDirtyFile(boolean dirtyFile) {
        this.dirtyFile = dirtyFile;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
