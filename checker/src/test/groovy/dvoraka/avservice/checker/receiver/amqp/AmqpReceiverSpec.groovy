package dvoraka.avservice.checker.receiver.amqp

import spock.lang.Specification

/**
 * AMQP receiver test.
 */
class AmqpReceiverSpec extends Specification {

    AmqpReceiver amqpReceiver


    def setup() {
        amqpReceiver = new AmqpReceiver("localhost")
    }
}
