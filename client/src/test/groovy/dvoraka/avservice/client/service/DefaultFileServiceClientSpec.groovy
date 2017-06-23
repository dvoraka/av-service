package dvoraka.avservice.client.service

import dvoraka.avservice.client.ServerComponent
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote file service spec.
 */
class DefaultFileServiceClientSpec extends Specification {

    @Subject
    DefaultFileServiceClient service

    ServerComponent serverComponent


    def setup() {
        serverComponent = Mock()
        service = new DefaultFileServiceClient(serverComponent)
    }

    def "save file"() {
        setup:
            AvMessage message = Utils.genSaveMessage()

        when:
            service.saveFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "save file with bad type"() {
        setup:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.saveFile(message)

        then:
            thrown(IllegalArgumentException)
    }

    def "load file"() {
        setup:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.loadFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "load file with bad type"() {
        setup:
            AvMessage message = Utils.genDeleteMessage()

        when:
            service.loadFile(message)

        then:
            thrown(IllegalArgumentException)
    }

    def "update file"() {
        setup:
            AvMessage message = Utils.genUpdateMessage()

        when:
            service.updateFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "update file with bad type"() {
        setup:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.updateFile(message)

        then:
            thrown(IllegalArgumentException)
    }

    def "delete file"() {
        setup:
            AvMessage message = Utils.genDeleteMessage()

        when:
            service.deleteFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "delete file with bad type"() {
        setup:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.deleteFile(message)

        then:
            thrown(IllegalArgumentException)
    }
}
