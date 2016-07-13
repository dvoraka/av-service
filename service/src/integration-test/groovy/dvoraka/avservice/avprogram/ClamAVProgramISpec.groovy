package dvoraka.avservice.avprogram

import dvoraka.avservice.common.Utils
import spock.lang.Shared
import spock.lang.Specification

/**
 * ClamAV program test.
 */
class ClamAVProgramISpec extends Specification {

    @Shared
    String eicarString = Utils.EICAR

    ClamAvProgram program


    def setup() {
        program = new ClamAvProgram()
    }

    def "connection test"() {
        expect:
        program.testConnection()
    }

    def "ping test"() {
        expect:
        program.ping()
    }

    def "scan normal bytes"() {
        expect:
        !program.scanStream("TESTDATA".getBytes())
    }

    def "scan infected bytes"() {
        expect:
        program.scanStream(eicarString.getBytes())
    }

    def "get version"() {
        setup:
        String stats = program.version()

        expect:
        stats
    }

    def "get stats"() {
        setup:
        String stats = program.stats()

        expect:
        stats
    }
}
