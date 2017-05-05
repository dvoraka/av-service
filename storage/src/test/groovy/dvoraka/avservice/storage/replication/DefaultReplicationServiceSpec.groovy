package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.data.ReplicationStatus
import dvoraka.avservice.common.replication.ReplicationHelper
import dvoraka.avservice.storage.FileServiceException
import dvoraka.avservice.storage.service.FileService
import spock.lang.Specification
import spock.lang.Subject

/**
 * Default replication service spec.
 */
class DefaultReplicationServiceSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultReplicationService service

    FileService fileService
    ReplicationServiceClient serviceClient
    ReplicationResponseClient responseClient
    RemoteLock remoteLock
    String nodeId = 'testID'
    String otherNodeId = 'otherID'


    def setup() {
        fileService = Mock()
        serviceClient = Mock()
        responseClient = Mock()
        remoteLock = Mock()

        service = new DefaultReplicationService(
                fileService, serviceClient, responseClient, remoteLock, nodeId)
        service.start()
    }

    def cleanup() {
        service.stop()
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
            ReplicationMessage request = createExistsRequest(filename, owner, nodeId)
            ReplicationMessage response = createExistsReply(request, otherNodeId)

        when:
            boolean result = service.exists(filename, owner)

        then:
            1 * fileService.exists(filename, owner)
            (1.._) * serviceClient.sendMessage(_)

            (1.._) * responseClient.getResponseWait(_, _) >> replicationList(response)

            result
    }

    def "get status"() {
        given:
            FileMessage message = Utils.genFileMessage()
            ReplicationMessage request = createStatusRequest(
                    message.getFilename(), message.getOwner())
            ReplicationMessage response = createOkStatusReply(request, otherNodeId)

        when:
            ReplicationStatus result = service.getStatus(message)

        then:
            (1.._) * serviceClient.sendMessage(_)

            (1.._) * responseClient.getResponseWait(_, _) >> replicationList(response)

        and: "not enough replicas"
            result == ReplicationStatus.FAILED
    }

    def "on message - save"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage saveRequest = createSaveMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(saveRequest)

        then:
            1 * fileService.saveFile(saveRequest)
            1 * serviceClient.sendMessage(_)
    }

    def "on message - failed save"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage saveRequest = createSaveMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(saveRequest)

        then:
            1 * fileService.saveFile(saveRequest) >> { throw new FileServiceException() }
            1 * serviceClient.sendMessage(_)
    }
}
