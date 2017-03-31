package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default remote lock implementation.
 */
@Component
public class DefaultRemoteLock implements RemoteLock {

    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;

//    private long sequence;


    @Autowired
    public DefaultRemoteLock(
            ReplicationServiceClient serviceClient,
            ReplicationResponseClient responseClient
    ) {
        this.serviceClient = serviceClient;
        this.responseClient = responseClient;
    }

    @Override
    public boolean lockForFile(String filename, String owner) throws InterruptedException {

        // send lock sequence query
        //
        // get actual lock sequence or create new one
        //
        // send lock query
        //
        // get lock query responses
        //
        // return locking status

        serviceClient.sendMessage(null);
        return true;
    }

    @Override
    public boolean unlockForFile(String filename, String owner) {

        // send unlock query
        //
        // get unlock query responses

        return true;
    }
}
