package dvoraka.avservice.client.kafka

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.amqp.AvMessageMapper
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.db.repository.db.DbMessageInfoRepository
import dvoraka.avservice.db.service.DbMessageInfoService
import org.apache.kafka.clients.consumer.ConsumerRecord
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
    AvMessageMapper messageMapper
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
            1 * listener.onAvMessage(message)
    }

    //TODO: add tests

    def "check service ID"() {
        expect:
            component.getServiceId() == testServiceId
    }

    AvMessageListener getAvMessageListener() {
        return new AvMessageListener() {
            @Override
            void onAvMessage(AvMessage message) {
            }
        }
    }
}
