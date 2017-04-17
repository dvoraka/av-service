package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.storage.service.FileService
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

/**
 * Default replication service spec.
 */
@Ignore
class DefaultReplicationServiceSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultReplicationService service

    FileService fileService
    ReplicationServiceClient serviceClient
    ReplicationResponseClient responseClient
    String nodeId = 'testId'


    def setup() {
        fileService = Mock()
        serviceClient = Mock()
        responseClient = Mock()

        service = new DefaultReplicationService(fileService, serviceClient, responseClient, nodeId)
    }

    def "save"() {
        given:
            FileMessage message = Utils.genFileMessage(MessageType.FILE_SAVE)
            ReplicationMessage saveMessage = createSaveMessage(message, nodeId, null)

        when:
            service.saveFile(message)

        then:
            (1.._) * fileService.exists(message.getFilename(), message.getOwner()) >> false

            (1.._) * serviceClient.sendMessage(_)

            (1.._) * responseClient.getResponseWait(_, _) >> {
                return Optional.ofNullable(null)
            } >> {
                ReplicationMessage response = createSaveSuccess(saveMessage, nodeId)

                return replicationList(response)
            }
    }

    Optional<ReplicationMessageList> replicationList(ReplicationMessage message) {
        ReplicationMessageList messages = new ReplicationMessageList()
        messages.add(message)

        return Optional.of(messages)
    }

    def "exists"() {
        given:
            String filename = 'testF'
            String owner = 'testO'

        when:
            service.exists(filename, owner)

        then:
            1 * fileService.exists(filename, owner)
            1 * serviceClient.sendMessage(_)

            1 * responseClient.getResponseWait(_, _) >> Utils.genExistsQueryMessage(filename, owner)
    }
}
