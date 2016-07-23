package dvoraka.avservice.avprogram

import spock.lang.Specification

/**
 * ClamAV program test.
 */
class ClamAvProgramSpec extends Specification {

    def "test connection"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> new Socket()
            program.ping() >> true

        expect:
            program.testConnection()
    }

    def "test connection without ping"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> new Socket()
            program.ping() >> false

        expect:
            !program.testConnection()
    }

    def "test connection with bad host"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> { throw new UnknownHostException() }

        expect:
            !program.testConnection()
    }

    def "test connection with bad connection"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> { throw new IOException() }

        expect:
            !program.testConnection()
    }

    def "test connection with security problem"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> { throw new SecurityException() }

        expect:
            !program.testConnection()
    }

    def "test connection with illegal socket values"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> { throw new IllegalArgumentException() }

        expect:
            !program.testConnection()
    }
}
