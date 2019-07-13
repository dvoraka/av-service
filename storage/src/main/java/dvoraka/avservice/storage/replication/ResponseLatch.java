package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.data.Message;

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

    public void await(long timeout) {
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
