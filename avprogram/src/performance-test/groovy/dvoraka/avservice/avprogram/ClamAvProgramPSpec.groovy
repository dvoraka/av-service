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


    def setup() {
        program = new ClamAvProgram()
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
}
