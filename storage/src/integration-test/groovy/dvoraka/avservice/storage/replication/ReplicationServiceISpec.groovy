package dvoraka.avservice.storage.replication

import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.common.helper.WaitingHelper
import dvoraka.avservice.common.util.Utils
import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import java.util.concurrent.Semaphore

/**
 * AMQP replication service spec.
 *
 * You can run real node with a Docker script in the tools directory called
 * <b>startReplicationNodes.sh</b> with argument 1 for starting one remote node.
 */
@Stepwise
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'storage-check', 'replication', 'client', 'amqp', 'no-db'])
@DirtiesContext
class ReplicationServiceISpec extends Specification implements FileServiceHelper, WaitingHelper {

    @Autowired
    ReplicationService service

    /**
     * Replication node count on a network.
     */
    @Shared
    int replicationNodes = 2
    @Shared
    int maxResponseTime = 3_000
    @Shared
    int fileCount = 100


    def setup() {
        waitUntil({ service.isRunning() })

        // the local node is a node too
        service.setReplicationCount(replicationNodes + 1)
        service.setMaxResponseTime(maxResponseTime)
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
            fileCount.times {
                service.saveFile(Utils.genSaveMessage())
            }

        then:
            notThrown(Exception)
    }

    @Ignore("manual testing")
    @Unroll
    def "save many files concurrently: #semaphoreSize threads"() {
        given:
            int taskCount = fileCount
            List<Thread> tasks = new ArrayList<>()
            Semaphore semaphore = new Semaphore(semaphoreSize)
            taskCount.times {
                tasks.add(new Thread({
                    FileMessage saveMessage = Utils.genSaveMessage()
                    // acquire semaphore
                    semaphore.acquire()
                    // save file
                    service.saveFile(saveMessage)
                    // check file
                    assert service.exists(saveMessage)
                    // release semaphore
                    semaphore.release()
                }))
            }

        when:
            tasks.each { it.start() }
            tasks.each { it.join() }

        then:
            notThrown(Exception)

        where:
            semaphoreSize << [64, 32, 16, 8, 4, 8, 16, 32, 64]
    }

    def "save many files and check"() {
        given:
            List<FileMessage> fileMessages = new ArrayList<>()
            int loops = fileCount

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
            int loops = fileCount

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
                assert service.loadFile(fileLoadMessage(it))
            }
    }

    def "save file and load many times"() {
        given:
            FileMessage message = Utils.genSaveMessage()
            FileMessage loadMessage = fileLoadMessage(message.getFilename(), message.getOwner())
            int loops = fileCount

        when:
            service.saveFile(message)

        then:
            service.exists(message)

        expect:
            loops.times {
                assert service.loadFile(loadMessage)
            }
    }

    def "save 1 MB file"() {
        given:
            int size = 1000 * 1000
            byte[] data = new byte[size]
            FileMessage saveMessage = getTestSaveMessage(data)

        when:
            service.saveFile(saveMessage)

        then:
            notThrown(Exception)
            service.exists(saveMessage)

        cleanup:
            service.deleteFile(fileDeleteMessage(saveMessage))
    }

    def "save 5 MB file"() {
        given:
            int size = 1000 * 1000 * 5
            byte[] data = new byte[size]
            FileMessage saveMessage = getTestSaveMessage(data)

        when:
            service.saveFile(saveMessage)

        then:
            notThrown(Exception)
            service.exists(saveMessage)

        cleanup:
            service.deleteFile(fileDeleteMessage(saveMessage))
            sleep(3_000)
    }

    def "save and delete file"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()

        when:
            service.saveFile(saveMessage)

        then:
            service.exists(saveMessage)

        when:
            service.deleteFile(
                    fileDeleteMessage(saveMessage.getFilename(), saveMessage.getOwner()))

        then:
            !service.exists(saveMessage)
    }

    def "save and update file"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            byte[] data = new byte[3]
            FileMessage updateMessage = fileUpdateMessage(saveMessage, data)

        when:
            service.saveFile(saveMessage)

        then:
            service.exists(saveMessage)

        when:
            service.updateFile(updateMessage)

        then:
            service.exists(updateMessage)

        when:
            FileMessage loaded = service.loadFile(fileLoadMessage(saveMessage))

        then:
            loaded.getFilename() == saveMessage.getFilename()
            loaded.getOwner() == saveMessage.getOwner()
            Arrays.equals(loaded.getData(), updateMessage.getData())
    }

    FileMessage getTestSaveMessage(byte[] data) {
        return fileSaveMessage('testF', 'testO', data)
    }
}
