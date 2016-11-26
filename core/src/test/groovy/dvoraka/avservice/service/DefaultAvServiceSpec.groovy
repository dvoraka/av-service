package dvoraka.avservice.service

import dvoraka.avservice.avprogram.AvProgram
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.exception.FileSizeException
import dvoraka.avservice.common.exception.ScanErrorException
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

/**
 * Default AV service spec.
 */
class DefaultAvServiceSpec extends Specification {

    AvProgram avProgram
    DefaultAvService service


    void setup() {
        avProgram = Mock()
        avProgram.getMaxArraySize() >> 100
        avProgram.getNoVirusResponse() >> Utils.OK_VIRUS_INFO

        service = new DefaultAvService(avProgram)
    }

    void cleanup() {
    }

    def "set max file size"() {
        given:
            long maxFileSize = 100

        when:
            service.setMaxFileSize(maxFileSize)

        then:
            service.getMaxFileSize() == maxFileSize
    }

    def "set max array size"() {
        given:
            long maxArraySize = 100

        when:
            service.setMaxArraySize(100)

        then:
            service.getMaxArraySize() == maxArraySize
    }

    def "scan bytes"() {
        when:
            service.scanBytes(new byte[size])

        then:
            calls * avProgram.scanBytes(_)

        where:
            size | calls
            10   | 1
            0    | 0
    }

    def "scan bytes with info"() {
        given:
            String expected = "INFO"
            avProgram.scanBytesWithInfo(_) >> expected

        when:
            String result = service.scanBytesWithInfo(new byte[10])

        then:
            result == expected
    }

    def "scan bytes with info without infection"() {
        given:
            avProgram.scanBytesWithInfo(_) >> Utils.OK_VIRUS_INFO

        when:
            String result = service.scanBytesWithInfo(new byte[10])

        then:
            result == Utils.OK_VIRUS_INFO
    }

    def "scan bytes with too big array"() {
        given:
            long bigSize = service.getMaxArraySize() + 1

        when:
            service.scanBytes(new byte[bigSize])

        then:
            thrown(ScanErrorException)
    }

    def "scan bytes with info with too big array"() {
        given:
            long bigSize = service.getMaxArraySize() + 1

        when:
            service.scanBytesWithInfo(new byte[bigSize])

        then:
            thrown(ScanErrorException)
    }

    def "scan empty bytes with info"() {
        when:
            String result = service.scanBytesWithInfo(new byte[0])

        then:
            result == ""
    }

    def "scan file without a file"() {
        when:
            service.scanFile(new File("123-TEST-FILE"))

        then:
            thrown(ScanErrorException)
    }

    def "scan file with a big file"() {
        setup:
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
            def throwable = thrown(ScanErrorException)
            throwable.getCause().getClass() == FileSizeException.class
            throwable.getCause().getMessage()
    }

    def "scan file with a normal file"() {
        setup:
            File tempFile = File.createTempFile("test-tempfile", ".tmp");
            Files.write(
                    tempFile.toPath(),
                    "data".getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND)
            tempFile.deleteOnExit()

        when:
            service.scanFile(tempFile)

        then:
            1 * avProgram.scanBytes(_)
    }
}
