package dvoraka.avservice.checker;

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

    private Sender sender;
    private Receiver receiver;

    private boolean dirtyFile;
    private String appId;

    private static Logger logger = LogManager.getLogger();

    public AVChecker(Sender sender, Receiver receiver) {
        this(sender, receiver, false, "antivirus");
    }

    public AVChecker(Sender sender, Receiver receiver,
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
            } else {
                logger.warn("Check problem - bad response");
            }
        } catch (ConnectException e) {
            System.err.println("Connection problem. Is message broker running?");
            logger.warn("Check problem - connection problem", e);
        } catch (IOException | InterruptedException | ProtocolException e) {
            logger.warn("Check problem.", e);
        } catch (LastMessageException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the sender
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public Receiver getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(Receiver receiver) {
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
