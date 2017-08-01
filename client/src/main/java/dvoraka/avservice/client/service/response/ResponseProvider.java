package dvoraka.avservice.client.service.response;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Generic response provider.
 *
 * @param <T> the type of the response
 */
public interface ResponseProvider<T> {

    /**
     * Returns a response.
     *
     * @param id the request ID
     * @return the response or null if response is not available
     */
    T getResponse(String id);

    /**
     * Waits if necessary for a response.
     *
     * @param id      the request ID
     * @param timeout the maximum time to wait
     * @param unit    the unit of the timeout
     * @return the response
     * @throws InterruptedException if the current thread was interrupted
     * @throws TimeoutException     if the wait timed out
     */
    T getResponse(String id, long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException;
}
