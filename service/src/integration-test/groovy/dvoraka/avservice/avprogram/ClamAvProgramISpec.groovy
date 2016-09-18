package dvoraka.avservice.avprogram

import dvoraka.avservice.common.Utils
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

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

    @Ignore("Performance and design testing")
    def "single thread performance"() {
        given:
            int filesCount = 200_000

        when:
            filesCount.times {
                program.scanBytes(eicarString.getBytes())
            }

        then:
            notThrown(Exception)
    }

    @Ignore("Performance testing")
    @Unroll
    def "threading performance: #threadsCount threads"() {
        given:
            int filesCount = 20_000
            int loops = filesCount / threadsCount
            Runnable scanBytes = {
                loops.times {
                    program.scanBytes(eicarString.getBytes())
                }
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

        where:
            threadsCount << [1, 2, 4, 6, 8, 10]
    }
}
