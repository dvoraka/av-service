package dvoraka.avservice.service

import dvoraka.avservice.avprogram.AvProgram
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

    def "scan bytes"() {
        when:
            service.scanBytes(new byte[10])

        then:
            1 * avProgram.scanBytes(_)
    }

    def "scan stream with info"() {
        given:
            String expected = "INFO"
            avProgram.scanBytesWithInfo(_) >> expected

        when:
            String result = service.scanBytesWithInfo(new byte[10])

        then:
            result == expected
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
