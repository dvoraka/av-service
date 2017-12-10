package dvoraka.avservice.client.kafka

import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.listener.AvMessageListener
import dvoraka.avservice.common.util.Utils
import dvoraka.avservice.db.repository.db.DbMessageInfoRepository
import dvoraka.avservice.db.service.DbMessageInfoService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.amqp.core.Message
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.converter.MessageConverter
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * Kafka adapter test.
 */
class KafkaAdapterSpec extends Specification {

    @Subject
    KafkaAdapter component

    KafkaTemplate<String, AvMessage> kafkaTemplate
    MessageConverter converter

    @Shared
    String testTopic = 'TEST-TOPIC'
    @Shared
    String testServiceId = 'testservice-1'


    def setup() {
        DbMessageInfoRepository infoRepository = Mock()
        DbMessageInfoService infoService = new DbMessageInfoService(infoRepository)

        converter = Mock()
        converter.fromMessage(_) >> Mock(AvMessage)

        kafkaTemplate = Mock()
        kafkaTemplate.getMessageConverter() >> converter

        component = new KafkaAdapter(testTopic, testServiceId, kafkaTemplate, infoService)
    }

    def "on message"() {
        given:
            AvMessageListener listener = Mock()
            AvMessage message = Utils.genMessage()

            component.addAvMessageListener(listener)

        when:
            component.onMessage(new ConsumerRecord<String, AvMessage>(
                    'testTopic', 0, 0, null, message))

        then:
            1 * listener.onMessage(message)
    }

    def "on message with null"() {
        when:
            component.onMessage((ConsumerRecord) null)

        then:
            thrown(NullPointerException)
    }

    def "send null message"() {
        when:
            component.sendAvMessage(null)

        then:
            thrown(NullPointerException)
    }

    def "send normal message"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            component.sendAvMessage(message)

        then:
            1 * kafkaTemplate.send(testTopic, message)
    }

    def "add listeners"() {
        when:
            component.addAvMessageListener(getAvMessageListener())
            component.addAvMessageListener(getAvMessageListener())

        then:
            component.getListenerCount() == 2
    }

    def "remove listeners"() {
        given:
            AvMessageListener listener1 = getAvMessageListener()
            AvMessageListener listener2 = getAvMessageListener()

        when:
            component.addAvMessageListener(listener1)
            component.addAvMessageListener(listener2)

        then:
            component.getListenerCount() == 2

        when:
            component.removeAvMessageListener(listener1)
            component.removeAvMessageListener(listener2)

        then:
            component.getListenerCount() == 0
    }

    def "add listeners from diff threads"() {
        given:
            int observers = 50

            Runnable addListener = {
                component.addAvMessageListener(getAvMessageListener())
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(addListener)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            observers == component.getListenerCount()
    }

    def "remove listeners from different threads"() {
        given:
            int observers = 50

            AvMessageListener messageListener = getAvMessageListener()
            Runnable removeListener = {
                component.removeAvMessageListener(messageListener)
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(removeListener)
            }

            observers.times {
                component.addAvMessageListener(messageListener)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            component.getListenerCount() == 0
    }

    def "run wrong onMessage"() {
        when:
            component.onMessage((Message) null)

        then:
            thrown(UnsupportedOperationException)
    }

    def "check service ID"() {
        expect:
            component.getServiceId() == testServiceId
    }

    AvMessageListener getAvMessageListener() {
        return new AvMessageListener() {
            @Override
            void onMessage(AvMessage message) {
            }
        }
    }
}
