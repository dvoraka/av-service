package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.storage.service.FileService
import spock.lang.Specification
import spock.lang.Subject

/**
 * Default replication service spec.
 */
class DefaultReplicationServiceSpec extends Specification {

    @Subject
    DefaultReplicationService service

    FileService fileService
    ReplicationServiceClient serviceClient
    ReplicationResponseClient responseClient


    def setup() {
        fileService = Mock()
        serviceClient = Mock()
        responseClient = Mock()

        service = new DefaultReplicationService(fileService, serviceClient, responseClient)
    }

    def "save"() {
        given:
            FileMessage message = Utils.genFileMessage(MessageType.FILE_SAVE)

        when:
            service.saveFile(message)

        then:
            (1.._) * fileService.exists(message.getFilename(), message.getOwner())
    }
}
