package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.AvMessage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

/**
 * Future for AvMessage response.
 */
public class AvMessageResponseFuture implements Future<AvMessage> {

    private final ResponseClient responseClient;
    private final String requestId;

    private volatile boolean cancelled;


    public AvMessageResponseFuture(ResponseClient responseClient, String requestId) {
        this.responseClient = requireNonNull(responseClient);
        this.requestId = requireNonNull(requestId);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        cancelled = true;

        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return cancelled || responseClient.getResponse(requestId) != null;
    }

    @Override
    public AvMessage get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public AvMessage get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {

        return responseClient.getResponse(requestId, timeout, unit);
    }
}
