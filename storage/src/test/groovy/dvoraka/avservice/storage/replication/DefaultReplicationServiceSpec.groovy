package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationResponseClient
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
}
