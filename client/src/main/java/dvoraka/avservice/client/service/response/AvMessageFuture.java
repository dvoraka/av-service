package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.AvMessage;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * AV message future.
 */
public interface AvMessageFuture extends Future<AvMessage> {

    @Override
    AvMessage get() throws InterruptedException;

    @Override
    AvMessage get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;
}
