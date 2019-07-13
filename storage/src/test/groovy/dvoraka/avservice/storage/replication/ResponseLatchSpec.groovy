package dvoraka.avservice.storage.replication

import dvoraka.avservice.common.data.replication.ReplicationMessage
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Timeout

import java.util.concurrent.CompletableFuture


class ResponseLatchSpec extends Specification {

    @Subject
    ResponseLatch<ReplicationMessage> latch


    @Timeout(2)
    def "test"() {
        given:
            String corrId = 'someId'
            latch = new ResponseLatch<>(corrId)

            Runnable anotherThread = {
                latch.setResponses(Collections.emptySet())
                latch.done()
            }

        expect:
            latch.getResponses() == null

        when:
            CompletableFuture.runAsync(anotherThread)
            latch.await(10_000)

        then:
            latch.getResponses().size() == 0
    }
}
