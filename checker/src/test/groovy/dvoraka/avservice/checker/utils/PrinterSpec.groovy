package dvoraka.avservice.checker.utils

import com.rabbitmq.client.AMQP
import spock.lang.Specification

/**
 * Printer test.
 */
class PrinterSpec extends Specification {

    def "print properties"() {
        when:
            Printer.printProperties(new AMQP.BasicProperties())

        then:
            notThrown(Exception)
    }

    def "print headers"() {
        given:
            Map<String, Object> map = new HashMap<>()
            map.put("Header key", "Header value")
            map.put("Header key 2", "Header value 2")

        when:
            Printer.printHeaders(map)

        then:
            notThrown(Exception)
    }
}
