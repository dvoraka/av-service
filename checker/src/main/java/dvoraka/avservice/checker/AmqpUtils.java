package dvoraka.avservice.checker;

import dvoraka.avservice.checker.exception.UnknownProtocolException;

/**
 * Created by dvoraka on 23.4.14.
 */
public interface AmqpUtils {

    String negotiateProtocol(String[] protocols) throws UnknownProtocolException;
}
