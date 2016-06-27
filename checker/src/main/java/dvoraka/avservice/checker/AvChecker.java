package dvoraka.avservice.checker;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.AvSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Diagnostic utility main class.
 */
public class AvChecker {

    @Autowired
    private AvSender sender;
    @Autowired
    private AvReceiver receiver;

    private boolean dirtyFile;
    private String appId;

    private static Logger log = LogManager.getLogger();


    public AvChecker(boolean dirtyFile, String appId) {
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
                log.warn("Check problem - bad response");
            }
        } catch (ConnectException e) {
            System.err.println("Connection problem. Is message broker running?");
            log.warn("Check problem - connection problem", e);
        } catch (IOException | InterruptedException | ProtocolException e) {
            log.warn("Check problem.", e);
        } catch (LastMessageException e) {
            log.info(e);
        }
    }

    /**
     * @return the sender
     */
    public AvSender getSender() {
        return sender;
    }

    /**
     * @return the receiver
     */
    public AvReceiver getReceiver() {
        return receiver;
    }

    /**
     * @return the dirtyFile
     */
    public boolean isDirtyFile() {
        return dirtyFile;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }
}
