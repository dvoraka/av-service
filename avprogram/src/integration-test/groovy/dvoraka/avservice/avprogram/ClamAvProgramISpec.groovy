package dvoraka.avservice.avprogram

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.exception.ScanException
import spock.lang.Shared
import spock.lang.Specification

/**
 * ClamAV program test.
 */
class ClamAvProgramISpec extends Specification {

    @Shared
    String eicarString = Utils.EICAR
    @Shared
    byte[] cleanData = "TESTDATA".getBytes()

    ClamAvProgram program
    ClamAvProgram programWithPooling


    def setup() {
        program = new ClamAvProgram()
        programWithPooling = new ClamAvProgram(
                ClamAvProgram.DEFAULT_HOST,
                ClamAvProgram.DEFAULT_PORT,
                ClamAvProgram.DEFAULT_MAX_ARRAY_SIZE,
                true
        )
    }

    def "is running"() {
        expect:
            program.isRunning()
    }

    def "ping test"() {
        expect:
            program.ping()
    }

    def "get version"() {
        setup:
            String version = program.version()

        expect:
            version
    }

    def "get stats"() {
        setup:
            String stats = program.stats()

        expect:
            stats
    }

    def "scan normal bytes"() {
        expect:
            !program.scanBytes(cleanData)
    }

    def "scan infected bytes"() {
        expect:
            program.scanBytes(eicarString.getBytes())
    }

    def "scan bytes new - normal message"() {
        expect:
            programWithPooling.scanBytesWithInfo(cleanData) == program.CLEAN_STREAM_RESPONSE
    }

    def "scan bytes new - infected message"() {
        expect:
            programWithPooling
                    .scanBytesWithInfo(eicarString.getBytes()) != program.CLEAN_STREAM_RESPONSE
    }

    def "scan too big array"() {
        given:
            long size = program.getMaxArraySize()

        when:
            program.scanBytes(new byte[size + 1])

        then:
            thrown(ScanException)
    }

    def "connection test"() {
        expect:
            program.testConnection()
    }

    def "concurrent test"() {
        given:
            // program config is MaxThreads * MaxConnectionQueueLength
            int threadsCount = 12 * 15
            Runnable scanBytes = {
                program.scanBytes(eicarString.getBytes())
            }

            Thread[] threads = new Thread[threadsCount]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(scanBytes)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            notThrown(Exception)
    }
}
