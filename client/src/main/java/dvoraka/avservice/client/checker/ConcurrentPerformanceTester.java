package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.service.ApplicationManagement;
import dvoraka.avservice.common.testing.PerformanceTest;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

/**
 * Concurrent performance tester.
 */
//TODO
@Component
public class ConcurrentPerformanceTester implements PerformanceTest, ApplicationManagement {

    private final AvNetworkComponent avNetworkComponent;
    private final PerformanceTestProperties testProperties;

    private static final Logger log = LogManager.getLogger(ConcurrentPerformanceTester.class);

    private final ConcurrentMap<String, Boolean> messages;

    private volatile boolean running;


    @Autowired
    public ConcurrentPerformanceTester(
            AvNetworkComponent avNetworkComponent,
            PerformanceTestProperties testProperties
    ) {
        this.avNetworkComponent = requireNonNull(avNetworkComponent);
        this.testProperties = requireNonNull(testProperties);

        messages = new ConcurrentHashMap<>();
    }

    @Override
    public void start() {
        running = true;

        avNetworkComponent.addMessageListener(this::onMessage);

        final long messageCount = testProperties.getMsgCount();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getResult() {
        return 0;
    }

    @Override
    public void run() {
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean passed() {
        return false;
    }

    private void onMessage(AvMessage message) {

    }
}
