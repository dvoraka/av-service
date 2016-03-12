package dvoraka.avservice;

/**
 * Base for the REST strategy.
 */
public interface RestStrategy {

    MessageStatus messageStatus(String id);
}
