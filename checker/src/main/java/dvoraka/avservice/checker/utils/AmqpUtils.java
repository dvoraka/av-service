package dvoraka.avservice.checker.utils;

import dvoraka.avservice.checker.exception.UnknownProtocolException;

/**
 * Interface for AMQP utilities.
 */
@FunctionalInterface
public interface AmqpUtils {

    String negotiateProtocol(String[] protocols) throws UnknownProtocolException;
}
