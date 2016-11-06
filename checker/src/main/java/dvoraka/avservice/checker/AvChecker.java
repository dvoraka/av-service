package dvoraka.avservice.checker;

import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.AvSender;
import dvoraka.avservice.common.exception.LastMessageException;
import dvoraka.avservice.common.exception.ProtocolException;
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

    private static final Logger log = LogManager.getLogger();

    private boolean dirtyFile;
    private String appId;


    public AvChecker(boolean dirtyFile, String appId) {
        this.dirtyFile = dirtyFile;
        this.appId = appId;
    }

    public void check() {
        try {
            String messageId = sender.sendFile(isDirtyFile(), getAppId());

            String result;
            if (isDirtyFile() == receiver.receive(messageId)) {
                // check OK
                result = "Test OK";
            } else {
                result = "Test failed";
                log.warn("Check problem - bad response");
            }
            System.out.println(result);
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
     * @return if the sending file is infected
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
