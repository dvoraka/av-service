package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.storage.replication.exception.NoResponseException;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseLatch<M extends Message> {

    private final CountDownLatch latch;
    private final String correlationID;

    private Set<M> responses;


    public ResponseLatch(String correlationID) {
        this.correlationID = correlationID;
        latch = new CountDownLatch(1);
    }

    public void await(long timeout) throws NoResponseException {
        try {
            boolean status = latch.await(timeout, TimeUnit.MILLISECONDS);

            if (!status) {
                throw new NoResponseException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NoResponseException();
        }
    }

    public void done() {
        latch.countDown();
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setResponses(Set<M> responses) {
        this.responses = responses;
    }

    public Set<M> getResponses() {
        return responses;
    }
}
