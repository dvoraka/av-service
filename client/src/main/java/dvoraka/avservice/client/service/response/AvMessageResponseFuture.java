package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.AvMessage;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

/**
 * Future for AvMessage response.
 */
public class AvMessageResponseFuture implements AvMessageFuture {

    /**
     * Auto-cancellation timeout.
     */
    private static final long MAX_TIMEOUT = 10_000;

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
    public AvMessage get() throws InterruptedException {
        AvMessage result;
        try {
            result = get(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new CancellationException("No response received in time.");
        }

        return result;
    }

    @Override
    public AvMessage get(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {

        AvMessage result = responseClient.getResponse(requestId, timeout, unit);

        if (cancelled) {
            throw new CancellationException();
        }

        return result;
    }
}
