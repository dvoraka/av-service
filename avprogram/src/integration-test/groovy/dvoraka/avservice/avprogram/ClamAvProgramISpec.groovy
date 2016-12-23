package dvoraka.avservice.avprogram

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.exception.ScanErrorException
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * ClamAV program test.
 */
class ClamAvProgramISpec extends Specification {

    @Shared
    String eicarString = Utils.EICAR

    ClamAvProgram program


    def setup() {
        program = new ClamAvProgram()
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
            !program.scanBytes("TESTDATA".getBytes())
    }

    def "scan infected bytes"() {
        expect:
            program.scanBytes(eicarString.getBytes())
    }

    @Ignore('WIP')
    def "scan bytes new"() {
        when:
            // warm up
            1000.times {
                program.scanBytesNew(eicarString.getBytes())
                program.scanBytesWithInfo(eicarString.getBytes())
            }

            int count = 100_000
            long start = System.currentTimeMillis()
            count.times {
                program.scanBytesNew(eicarString.getBytes())
            }
            println(System.currentTimeMillis() - start)

            start = System.currentTimeMillis()
            count.times {
                program.scanBytesWithInfo(eicarString.getBytes())
            }
            println(System.currentTimeMillis() - start)

        then:
            true
    }

    def "scan too big array"() {
        given:
            long size = program.getMaxArraySize()

        when:
            program.scanBytes(new byte[size + 1])

        then:
            thrown(ScanErrorException)
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
