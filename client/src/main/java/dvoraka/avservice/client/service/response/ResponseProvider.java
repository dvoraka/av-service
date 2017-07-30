package dvoraka.avservice.client.service.response;

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
}
