package dvoraka.avservice.avprogram

import dvoraka.avservice.common.exception.ScanErrorException
import spock.lang.Specification

/**
 * ClamAV program test.
 */
class ClamAvProgramSpec extends Specification {

    def "scan stream (empty array)"() {
        given:
            ClamAvProgram program = Spy()
            program.scanStreamWithInfo(_) >> ClamAvProgram.CLEAN_STREAM_RESPONSE

        when:
            boolean result = program.scanStream(new byte[0])

        then:
            !result
    }

    def "scan stream (empty array) with false check"() {
        given:
            ClamAvProgram program = Spy()
            program.scanStreamWithInfo(_) >> "VIRUS"

        when:
            boolean result = program.scanStream(new byte[0])

        then:
            result
    }

    def "scan stream with an exception"() {
        given:
            ClamAvProgram program = Spy()
            program.scanStreamWithInfo(_) >> {
                throw new ScanErrorException("TEST")
            }

        when:
            program.scanStream(new byte[0])

        then:
            thrown(ScanErrorException)
    }

    def "scan stream with with info with IO exception"() {
        given:
            ClamAvProgram program = Spy()
            program.createSocket() >> {
                throw new IOException("TEST")
            }

        when:
            program.scanStreamWithInfo(new byte[0])

        then:
            thrown(ScanErrorException)
    }

    def "is running"() {
        setup:
            ClamAvProgram program = Spy()
            program.testConnection() >> true

        expect:
            program.isRunning()
    }

    def "ping with IO exception"() {
        setup:
            ClamAvProgram program = Spy()
            program.createSocket() >> {
                throw new IOException("TEST")
            }

        expect:
            !program.ping()
    }

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
