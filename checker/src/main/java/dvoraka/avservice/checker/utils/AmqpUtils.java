package dvoraka.avservice.checker.utils;

import dvoraka.avservice.checker.exception.UnknownProtocolException;

/**
 * Created by dvoraka on 23.4.14.
 */
@FunctionalInterface
public interface AmqpUtils {

    String negotiateProtocol(String[] protocols) throws UnknownProtocolException;
}
