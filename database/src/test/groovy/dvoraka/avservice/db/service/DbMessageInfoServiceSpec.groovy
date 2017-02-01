package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.db.model.MessageInfo
import dvoraka.avservice.db.repository.db.DbMessageInfoRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant
import java.util.stream.Stream

/**
 * Default message info service spec.
 */
class DbMessageInfoServiceSpec extends Specification {

    @Subject
    DbMessageInfoService messageService

    DbMessageInfoRepository messageRepository

    String testUuid = 'AAA'
    String testService = 'TEST'


    def setup() {
        messageRepository = Mock()
        messageService = new DbMessageInfoService(messageRepository)
    }

    def "save"() {
        when:
            messageService.save(Utils.genNormalMessage(), AvMessageSource.TEST, testService)

        then:
            1 * messageRepository.save(_ as MessageInfo)
    }

    def "load info"() {
        when:
            messageService.loadInfo(testUuid)

        then:
            1 * messageRepository.findByUuid(_) >> createTestInfo(testUuid)
    }

    def "load info stream"() {
        given:
            String uuid = testUuid
            Instant now = Instant.now()
            List<MessageInfo> infos = Arrays.asList(
                    createTestInfo(uuid), createTestInfo(uuid))

        when:
            Stream<AvMessageInfo> info = messageService.loadInfoStream(now, now)

        then:
            1 * messageRepository.findByCreatedBetween(_, _) >> infos
            info.count() == infos.size()
    }

    MessageInfo createTestInfo(String uuid) {
        MessageInfo testInfo = new MessageInfo()
        testInfo.setUuid(uuid)
        testInfo.setSource(AvMessageSource.TEST.toString())
        testInfo.setCreated(new Date())

        return testInfo
    }
}
