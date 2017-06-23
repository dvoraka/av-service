package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.data.ReplicationStatus
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.common.replication.ReplicationHelper
import dvoraka.avservice.storage.exception.ExistingFileException
import dvoraka.avservice.storage.exception.FileNotFoundException
import dvoraka.avservice.storage.exception.FileServiceException
import dvoraka.avservice.storage.service.FileService
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * Default replication service spec.
 */
class DefaultReplicationServiceSpec extends Specification
        implements ReplicationHelper, FileServiceHelper {

    @Subject
    DefaultReplicationService service

    FileService fileService
    ReplicationServiceClient serviceClient
    ReplicationResponseClient responseClient
    RemoteLock remoteLock

    @Shared
    String nodeId = 'testID'
    @Shared
    String otherNodeId = 'otherID'
    @Shared
    String filename = 'testFilename'
    @Shared
    String owner = 'testOwner'


    def setup() {
        fileService = Mock()
        serviceClient = Mock()
        responseClient = Mock()
        remoteLock = Mock()

        service = new DefaultReplicationService(
                fileService, serviceClient, responseClient, remoteLock, nodeId)
    }

    def cleanup() {
        service.stop()
    }

    def "start"() {
        when:
            service.start()
            sleep(50)

        then:
            1 * responseClient.addNoResponseMessageListener(_)
    }

    def "start with discovery response"() {
        when:
            service.start()
            sleep(350)

        then:
            1 * responseClient.addNoResponseMessageListener(_)

            1 * responseClient.getResponseWait(_, _, _) >> replicationList(
                    createDiscoverReply(
                            createDiscoverRequest(nodeId), otherNodeId))
    }

    def "stop"() {
        when:
            service.stop()

        then:
            1 * responseClient.removeNoResponseMessageListener(_)
    }

    def "save"() {
        given:
            service.setReplicationCount(1)
            FileMessage message = Utils.genFileMessage(MessageType.FILE_SAVE)

        when:
            service.saveFile(message)

        then:
            2 * fileService.exists(message.getFilename(), message.getOwner()) >> false
            1 * serviceClient.sendMessage(_)

            2 * responseClient.getResponseWaitSize(_, _, _) >> {
                return Optional.ofNullable(null)
            }

            1 * remoteLock.lockForFile(message.getFilename(), message.getOwner(), 0) >> true
            1 * remoteLock.unlockForFile(message.getFilename(), message.getOwner(), 0)
    }

    def "save with existing local file"() {
        given:
            FileMessage message = Utils.genFileMessage(MessageType.FILE_SAVE)
            fileService.exists(message.getFilename(), message.getOwner()) >> true

        when:
            service.saveFile(message)

        then:
            thrown(ExistingFileException)
    }

    def "load with existing local file"() {
        given:
            FileMessage message = Utils.genFileMessage(MessageType.FILE_LOAD)

        when:
            service.loadFile(message)

        then:
            1 * fileService.exists(message.getFilename(), message.getOwner()) >> true

            1 * remoteLock.lockForFile(message.getFilename(), message.getOwner(), _) >> true
            1 * fileService.loadFile(_)

            1 * remoteLock.unlockForFile(message.getFilename(), message.getOwner(), _)
    }

    @Ignore('WIP')
    def "load without existing local file"() {
        given:
            FileMessage message = Utils.genFileMessage(MessageType.FILE_LOAD)

        when:
            service.loadFile(message)

        then:
            2 * fileService.exists(message.getFilename(), message.getOwner()) >> false
            1 * responseClient.getResponseWait(_, _) >> replicationList(
                    createExistsReply(
                            createExistsRequest(message.getFilename(), message.getOwner(), nodeId),
                            otherNodeId
                    )
            )

            1 * responseClient.getResponseWait(_, _) >> replicationList(
                    createLoadSuccess(Mock(FileMessage), otherNodeId, nodeId)
            )
    }

    def "load without existing file"() {
        given:
            FileMessage message = Utils.genFileMessage(MessageType.FILE_LOAD)

        when:
            service.loadFile(message)

        then:
            2 * fileService.exists(message.getFilename(), message.getOwner()) >> false

            1 * remoteLock.lockForFile(message.getFilename(), message.getOwner(), _) >> true

            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWaitSize(_, _, _) >> {
                return Optional.ofNullable(null)
            }

            1 * remoteLock.unlockForFile(message.getFilename(), message.getOwner(), _)

            thrown(FileNotFoundException)
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
            1 * serviceClient.sendMessage(_)

            1 * responseClient.getResponseWaitSize(_, _, _) >> replicationList(response)

            result
    }

    def "who has"() {
        given:
            String filename = 'testF'
            String owner = 'testO'
            ReplicationMessage request = createExistsRequest(filename, owner, nodeId)
            ReplicationMessage response = createExistsReply(request, otherNodeId)

        when:
            Set<String> ids = service.whoHas(filename, owner)

        then:
            1 * serviceClient.sendMessage(_)

            1 * responseClient.getResponseWaitSize(_, _, _) >> replicationList(response)

            ids.size() == 1
    }

    def "get status"() {
        given:
            FileMessage message = Utils.genFileMessage()
            ReplicationMessage request = createStatusRequest(
                    message.getFilename(), message.getOwner(), "")
            ReplicationMessage response = createOkStatusReply(request, otherNodeId)

        when:
            ReplicationStatus result = service.getStatus(message)

        then:
            1 * serviceClient.sendMessage(_)

            1 * responseClient.getResponseWaitSize(_, _, _) >> replicationList(response)

        and: "not enough replicas"
            result == ReplicationStatus.FAILED
    }

    def "on message - discover"() {
        given:
            ReplicationMessage discoverRequest = createDiscoverRequest(nodeId)

        when:
            service.onMessage(discoverRequest)

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message - exists"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage existsRequest =
                    createExistsRequest(fileMessage.getFilename(), fileMessage.getOwner(), nodeId)

        when:
            service.onMessage(existsRequest)

        then:
            1 * fileService.exists(existsRequest.getFilename(), existsRequest.getOwner()) >> true
            1 * serviceClient.sendMessage(_)
    }

    def "on message - exists failed"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage existsRequest =
                    createExistsRequest(fileMessage.getFilename(), fileMessage.getOwner(), nodeId)

        when:
            service.onMessage(existsRequest)

        then:
            1 * fileService.exists(existsRequest.getFilename(), existsRequest.getOwner()) >> false
            1 * serviceClient.sendMessage(_)
    }

    def "on message - save"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage saveRequest = createSaveMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(saveRequest)

        then:
            1 * fileService.saveFile(_)
            1 * serviceClient.sendMessage(_)
    }

    def "on message - save failed"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage saveRequest = createSaveMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(saveRequest)

        then:
            1 * fileService.saveFile(_) >> { throw new FileServiceException() }
            1 * serviceClient.sendMessage(_)
    }

    def "on message - load"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage loadRequest = createLoadMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(loadRequest)

        then:
            1 * fileService.loadFile(_) >> Utils.genSaveMessage()
            1 * serviceClient.sendMessage(_)
    }

    def "on message - load failed"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage loadRequest = createLoadMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(loadRequest)

        then:
            1 * fileService.loadFile(_) >> { throw new FileServiceException() }
            1 * serviceClient.sendMessage(_)
    }

    def "on message - delete"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage deleteRequest = createDeleteMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(deleteRequest)

        then:
            1 * fileService.deleteFile(_)
            1 * serviceClient.sendMessage(_)
    }

    def "on message - delete failed"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage deleteRequest = createDeleteMessage(fileMessage, nodeId, otherNodeId)

        when:
            service.onMessage(deleteRequest)

        then:
            1 * fileService.deleteFile(_) >> { throw new FileServiceException() }
            1 * serviceClient.sendMessage(_)
    }

    Optional<ReplicationMessageList> replicationList(ReplicationMessage message) {
        ReplicationMessageList messages = new ReplicationMessageList()
        messages.add(message)

        return Optional.of(messages)
    }
}
