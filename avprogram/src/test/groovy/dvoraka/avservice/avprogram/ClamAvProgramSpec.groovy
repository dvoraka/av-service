package dvoraka.avservice.avprogram

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.exception.ScanException
import dvoraka.avservice.common.service.CachingService
import spock.lang.Specification
import spock.lang.Subject

/**
 * ClamAV program spec.
 */
class ClamAvProgramSpec extends Specification {

    @Subject
    ClamAvProgram avProgram


    def setup() {
        avProgram = new ClamAvProgram()
    }

    def "parse response"() {
        expect:
            avProgram.parseResponse(input) == result

        where:
            input                   | result
            ''                      | ''
            'string'                | ''
            ':'                     | ''
            'abc :def'              | ''
            ': '                    | ''
            ' :  '                  | ' '
            'abc: def'              | 'def'
            'abc:def'               | ''
            'abc: def: ghi'         | 'def: ghi'
            '1: stream: OK'         | 'stream: OK'
            '123: stream: OK'       | 'stream: OK'
            '123456789: stream: OK' | 'stream: OK'
    }

    def "scan bytes (empty array)"() {
        given:
            ClamAvProgram program = Spy()
            program.scanBytesWithInfo(_) >> ClamAvProgram.CLEAN_STREAM_RESPONSE

        when:
            boolean result = program.scanBytes(new byte[0])

        then:
            !result
    }

    def "scan bytes (empty array) with false check"() {
        given:
            ClamAvProgram program = Spy()
            program.scanBytesWithInfo(_) >> "VIRUS"

        when:
            boolean result = program.scanBytes(new byte[0])

        then:
            result
    }

    def "scan bytes with exception"() {
        given:
            ClamAvProgram program = Spy()
            program.scanBytesWithInfo(_) >> {
                throw new ScanException("TEST")
            }

        when:
            program.scanBytes(new byte[0])

        then:
            thrown(ScanException)
    }

    def "scan bytes with info with IO exception"() {
        given:
            ClamAvProgram program = Spy()
            program.createSocket() >> {
                throw new IOException("TEST")
            }

        when:
            program.scanBytesWithInfo(new byte[0])

        then:
            thrown(ScanException)
    }

    def "get no virus response string"() {
        expect:
            avProgram.getNoVirusResponse() == avProgram.CLEAN_STREAM_RESPONSE
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

    def "get no virus string"() {
        expect:
            avProgram.getNoVirusResponse() == Utils.OK_VIRUS_INFO
    }

    def "set and get caching flag"() {
        setup:
            avProgram.setCachingService(Mock(CachingService))

        when:
            avProgram.setCaching(false)

        then:
            !avProgram.isCaching()

        when:
            avProgram.setCaching(true)

        then:
            avProgram.isCaching()
    }

    def "set and get caching flag without caching service"() {
        when:
            avProgram.setCaching(false)

        then:
            !avProgram.isCaching()

        when:
            avProgram.setCaching(true)

        then:
            !avProgram.isCaching()
    }
}
