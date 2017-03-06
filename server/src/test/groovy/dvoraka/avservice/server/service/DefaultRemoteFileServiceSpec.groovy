package dvoraka.avservice.server.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.server.ServerComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote file service spec.
 */
class DefaultRemoteFileServiceSpec extends Specification {

    @Subject
    DefaultRemoteFileService service

    ServerComponent serverComponent


    def setup() {
        serverComponent = Mock()
        service = new DefaultRemoteFileService(serverComponent)
    }

    def "save file"() {
        setup:
            AvMessage message = Utils.genSaveMessage()

        when:
            service.saveFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "load file"() {
        setup:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.loadFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "update file"() {
        setup:
            AvMessage message = Utils.genUpdateMessage()

        when:
            service.updateFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "delete file"() {
        setup:
            AvMessage message = Utils.genDeleteMessage()

        when:
            service.deleteFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }
}
