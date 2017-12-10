package dvoraka.avservice.stats

import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.InfoSource
import dvoraka.avservice.common.util.Utils
import spock.lang.Specification
import spock.lang.Subject

import java.util.stream.Stream

/**
 * Service spec.
 */
class DefaultStatsServiceSpec extends Specification {

    @Subject
    DefaultStatsService service

    Messages messages


    def setup() {
        messages = Mock()
        service = new DefaultStatsService(messages)
    }

    def "today count"() {
        given:
            long count = 5
            Stream<AvMessageInfo> infoStream = Stream
                    .generate({ Utils.genAvMessageInfo(InfoSource.PROCESSOR) })
                    .limit(count)

            messages.when(_, _) >> infoStream

        expect:
            count == service.todayCount()
    }
}
