package dvoraka.avservice;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * AMQP AV server.
 */
public class AmqpAVServer extends AbstractAVServer implements AVServer {

    @Autowired
    private MessageProcessor messageProcessor;


    public static void main(String[] args) {
        System.out.println("AMQP server");

        AmqpAVServer server = new AmqpAVServer();

        Runnable listenJob = server::listen;
        Runnable responseJob = server::response;

        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(listenJob);
        pool.execute(responseJob);
        pool.shutdown();
        try {
            pool.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();
    }

    private void listen() {
        while (true) {
            if (isStopped()) {
                break;
            }

            System.out.println("Listening...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void response() {
        while (true) {
            if (isStopped()) {
                break;
            }

            System.out.println("Sending...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        setStarted();
    }

    @Override
    public void stop() {
        setStopped();
    }

    @Override
    public void restart() {

    }
}
