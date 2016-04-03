package dvoraka.avservice.service

import dvoraka.avservice.avprogram.AVProgram
import dvoraka.avservice.exception.ScanErrorException
import spock.lang.Specification

/**
 * Default AV service test.
 */
class DefaultAVServiceSpec extends Specification {

    DefaultAVService service


    void setup() {
        service = new DefaultAVService()
    }

    void cleanup() {
    }

    def "scan stream"() {
        setup:
        AVProgram program = Mock()
        service.setAvProgram(program)

        when:
        service.scanStream(new byte[10])

        then:
        1 * program.scanStream(_)
    }

    def "scan stream with info"() {
        setup:
        AVProgram program = Stub()

        program.scanStreamWithInfo(_) >> "INFO"

        service.setAvProgram(program)

        expect:
        service.scanStreamWithInfo(new byte[10]).equals("INFO")
    }

    def "scan file without file"() {
        setup:
        AVProgram program = Mock()
        service.setAvProgram(program)

        when:
        service.scanFile(new File("123-TEST-FILE"))

        then:
        thrown(ScanErrorException)
    }
}
