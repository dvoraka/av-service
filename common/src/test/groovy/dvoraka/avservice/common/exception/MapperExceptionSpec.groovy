package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * MapperException spec.
 */
class MapperExceptionSpec extends Specification {

    def "with message"() {
        given:
            String message = "TEST"
            MapperException exception = new MapperException(message)

        when:
            throw exception

        then:
            def e = thrown(MapperException)
            e.getMessage() == message
    }
}
