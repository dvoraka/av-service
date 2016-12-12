package dvoraka.avservice.common.service

import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

/**
 * Timed storage spec.
 */
class TimedStorageSpec extends Specification {

    @Subject
    TimedStorage<String> storage

    String testData = 'data'
    PollingConditions conditions


    def setup() {
        storage = new TimedStorage<>(10_000)
        conditions = new PollingConditions(timeout: 3)
    }

    def "insert data"() {
        when:
            long size = storage.size()

        then:
            size == 0L

        when:
            storage.put(testData)
            storage.put(testData)

        then:
            storage.size() == 1L
            storage.contains(testData)
    }

    def "remove data"() {
        when:
            storage.put(testData)
            storage.put(testData)

        then:
            storage.size() == 1L
            storage.contains(testData)

        when:
            storage.remove(testData)

        then:
            storage.size() == 0L
            !storage.contains(testData)
    }

    def "test cleaning"() {
        given:
            storage = new TimedStorage<>(1)
            int dataCount = 10

        when:
            dataCount.times {
                storage.put(testData + it)
            }

        then:
            conditions.eventually {
                storage.size() == 0
            }
    }
}
