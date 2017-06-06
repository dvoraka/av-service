package dvoraka.avservice.avprogram

import dvoraka.avservice.common.Utils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Performance test for ClamAvProgram.
 */
class ClamAvProgramPSpec extends Specification {

    @Shared
    String eicarString = Utils.EICAR

    ClamAvProgram program
    ClamAvProgram programWithPooling


    def setup() {
        program = new ClamAvProgram()
        programWithPooling = new ClamAvProgram(
                ClamAvProgram.HOST,
                ClamAvProgram.PORT,
                ClamAvProgram.MAX_ARRAY_SIZE,
                true
        )
    }

    def "single thread performance"() {
        given:
            int fileCount = 200_000

        when:
            fileCount.times {
                program.scanBytes(eicarString.getBytes())
            }

        then:
            notThrown(Exception)
    }

    def "scan bytes new and old - performance comparison"() {
        when:
            // warm up
            1000.times {
                programWithPooling.scanBytesWithInfo(eicarString.getBytes())
                program.scanBytesWithInfo(eicarString.getBytes())
            }

            int count = 100_000
            long start = System.currentTimeMillis()
            count.times {
                programWithPooling.scanBytesWithInfo(eicarString.getBytes())
            }
            println(System.currentTimeMillis() - start)

            start = System.currentTimeMillis()
            count.times {
                program.scanBytesWithInfo(eicarString.getBytes())
            }
            println(System.currentTimeMillis() - start)

        then:
            notThrown(Exception)
    }

    def "scan bytes new - performance"() {
        when:
            // warm up
            1000.times {
                programWithPooling.scanBytesWithInfo(eicarString.getBytes())
            }

            int count = 100_000
            long start = System.currentTimeMillis()
            count.times {
                programWithPooling.scanBytesWithInfo(eicarString.getBytes())
            }
            println(System.currentTimeMillis() - start)

        then:
            notThrown(Exception)
    }

    def "scan bytes new - check response"() {
        when:
            int count = 1_000_000
            long start = System.currentTimeMillis()
            count.times {
                assert programWithPooling.scanBytesWithInfo(
                        eicarString.getBytes()) == 'stream: Eicar-Test-Signature FOUND'
            }
            println(System.currentTimeMillis() - start)

        then:
            notThrown(Exception)
    }

    @Unroll
    def "threading performance: #threadCount threads"() {
        given:
            int filesCount = 20_000
            int loops = filesCount / threadCount

            Runnable scanBytes = {
                loops.times {
                    program.scanBytes(eicarString.getBytes())
                }
            }

            Thread[] threads = new Thread[threadCount]
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
            threadCount << [1, 2, 4, 6, 8, 10]
    }

    @Unroll
    def "stream length test: #size bytes"() {
        setup:
            byte[] bytes = new byte[size];

        expect:
            !programWithPooling.scanBytes(bytes)

        where:
            size << [100, 1000, 10_000, 100_000, 1000_000, 10_000_000]
    }
}
