package dvoraka.avservice.storage.replication

import dvoraka.avservice.common.FileServiceHelper
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * AMQP replication service spec.
 *
 * Complete test is in the server module. This could be for some testing without a server
 * infrastructure. You can run real node with a Docker script in the tools directory called
 * <b>startReplicationNodes.sh</b> with argument 1 for starting one remote node.
 */
@Ignore
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication', 'client', 'amqp', 'no-db'])
class ReplicationServiceISpec extends Specification implements FileServiceHelper {

    @Autowired
    ReplicationService service

    /**
     * Replication node count on a network.
     */
    @Shared
    int replicationNodes = 3


    def setup() {
        // the local node is a node too
        service.setReplicationCount(replicationNodes + 1)
        // wait for initialization
        sleep(2_000)
    }

    def cleanup() {
    }

    def "test configuration"() {
        expect:
            true
    }

    def "save file"() {
        expect:
            service.saveFile(Utils.genSaveMessage())
    }

    def "save file and check"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()

        when:
            service.saveFile(saveMessage)

        then:
            service.exists(saveMessage)
    }

    def "save many files"() {
        when:
            100.times {
                service.saveFile(Utils.genSaveMessage())
            }

        then:
            notThrown(Exception)
    }

    def "save many files and check"() {
        given:
            List<FileMessage> fileMessages = new ArrayList<>()
            int loops = 100

        when:
            loops.times {
                FileMessage message = Utils.genSaveMessage()
                fileMessages.add(message)
                service.saveFile(message)
            }

        then:
            notThrown(Exception)

        expect:
            fileMessages.forEach() {
                assert service.exists(it)
            }
    }

    def "save and load file"() {
        given:
            FileMessage message = Utils.genSaveMessage()
            FileMessage loadMessage = fileLoadMessage(message.getFilename(), message.getOwner())

        when:
            service.saveFile(message)

        then:
            service.exists(message)

        when:
            FileMessage loaded = service.loadFile(loadMessage)

        then:
            loaded.getFilename() == message.getFilename()
            loaded.getOwner() == message.getOwner()
            Arrays.equals(loaded.getData(), message.getData())
    }

    def "save many files and load"() {
        given:
            List<FileMessage> fileMessages = new ArrayList<>()
            int loops = 100

        when:
            loops.times {
                FileMessage message = Utils.genSaveMessage()
                fileMessages.add(message)
                service.saveFile(message)
            }

        then:
            notThrown(Exception)

        expect:
            fileMessages.forEach() {
                assert service.loadFile(it)
            }
    }
}
