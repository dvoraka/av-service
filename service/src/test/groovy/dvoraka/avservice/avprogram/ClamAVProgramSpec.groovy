package dvoraka.avservice.avprogram

import spock.lang.Specification

/**
 * ClamAV program test.
 */
class ClamAVProgramSpec extends Specification {

    def "test connection"() {
        setup:
        ClamAVProgram program = Spy()
        program.createSocket() >> new Socket()
        program.ping() >> true

        expect:
        program.testConnection()
    }

    def "test connection without ping"() {
        setup:
        ClamAVProgram program = Spy()
        program.createSocket() >> new Socket()
        program.ping() >> false

        expect:
        !program.testConnection()
    }

    def "test connection with bad host"() {
        setup:
        ClamAVProgram program = Spy()
        program.createSocket() >> { throw new UnknownHostException() }

        expect:
        !program.testConnection()
    }

    def "test connection with bad connection"() {
        setup:
        ClamAVProgram program = Spy()
        program.createSocket() >> { throw new IOException() }

        expect:
        !program.testConnection()
    }

    def "test connection with security problem"() {
        setup:
        ClamAVProgram program = Spy()
        program.createSocket() >> { throw new SecurityException() }

        expect:
        !program.testConnection()
    }

    def "test connection with illegal socket values"() {
        setup:
        ClamAVProgram program = Spy()
        program.createSocket() >> { throw new IllegalArgumentException() }

        expect:
        !program.testConnection()
    }
}
