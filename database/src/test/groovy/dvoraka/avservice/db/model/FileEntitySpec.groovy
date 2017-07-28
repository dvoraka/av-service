package dvoraka.avservice.db.model

import dvoraka.avservice.common.data.FileMessage
import spock.lang.Specification
import spock.lang.Subject

/**
 * File spec.
 */
class FileEntitySpec extends Specification {

    @Subject
    FileEntity file


    def setup() {
        file = new FileEntity()
    }

    def "set and get"() {
        given:
            Long dbId = 9
            byte[] data = new byte[10]
            String filename = 'testFilename'
            String owner = 'testOwner'

        when:
            file.with {
                setId(dbId)
                setData(data)
                setFilename(filename)
            }
            file.setOwner(owner)

        then:
            with(file) {
                getId() == dbId
                Arrays.equals(getData(), data)
                getFilename() == filename
            }
            file.getOwner() == owner
    }

    def "create file message"() {
        given:
            Long dbId = 9
            byte[] data = new byte[10]
            String filename = 'testFilename'
            String owner = 'testOwner'
            String testCorrId = 'testId'

        when:
            file.with {
                setId(dbId)
                setData(data)
                setFilename(filename)
            }
            file.setOwner(owner)

            FileMessage fileMessage = file.fileMessage(testCorrId)

        then:
            fileMessage.getCorrelationId() == testCorrId
            Arrays.equals(fileMessage.getData(), data)
            fileMessage.getFilename() == filename
            fileMessage.getOwner() == owner
    }
}
