package dvoraka.avservice.service

import dvoraka.avservice.avprogram.AvProgram
import dvoraka.avservice.exception.FileSizeException
import dvoraka.avservice.exception.ScanErrorException
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.StandardOpenOption

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

    def "constructor with max file size"() {
        setup:
        long maxFileSize = 100
        service = new DefaultAVService(maxFileSize)

        expect:
        service.getMaxFileSize() == maxFileSize
    }

    def "scan stream"() {
        setup:
        AvProgram program = Mock()
        service.setAvProgram(program)

        when:
        service.scanStream(new byte[10])

        then:
        1 * program.scanStream(_)
    }

    def "scan stream with info"() {
        setup:
        AvProgram program = Stub()

        program.scanStreamWithInfo(_) >> "INFO"

        service.setAvProgram(program)

        expect:
        service.scanStreamWithInfo(new byte[10]).equals("INFO")
    }

    def "scan file without a file"() {
        setup:
        AvProgram program = Mock()
        service.setAvProgram(program)

        when:
        service.scanFile(new File("123-TEST-FILE"))

        then:
        thrown(ScanErrorException)
    }

    def "scan file with a big file"() {
        setup:
        AvProgram program = Mock()
        service.setAvProgram(program)

        File bigTempFile = File.createTempFile("test-tempfile", ".tmp");
        bigTempFile.deleteOnExit()

        long maxFileSize = service.getMaxFileSize()

        byte[] buffer = new byte[1000]
        long actualSize = 0
        while (!(actualSize > maxFileSize)) {
            Files.write(bigTempFile.toPath(), buffer, StandardOpenOption.APPEND)
            actualSize = Files.size(bigTempFile.toPath())
        }

        when:
        service.scanFile(bigTempFile)

        then:
        actualSize > maxFileSize
        thrown(FileSizeException)
    }

    def "scan file with a file"() {
        setup:
        AvProgram program = Mock()
        service.setAvProgram(program)

        File tempFile = File.createTempFile("test-tempfile", ".tmp");
        tempFile.deleteOnExit()

        when:
        service.scanFile(tempFile)

        then:
        1 * program.scanStream(_)
    }
}
