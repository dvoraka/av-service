package dvoraka.avservice.checker;

/**
 * Created by dvoraka on 23.4.14.
 */
public interface AmqpUtils {

    String negotiateProtocol(String[] protocols) throws UnknownProtocolException;
}
