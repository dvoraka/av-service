package dvoraka.avservice.db.repository.db

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.db.configuration.DatabaseConfig
import dvoraka.avservice.db.model.FileEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles(['db'])
class DbFileRepositoryISpec extends Specification {

    @Autowired
    DbFileRepository repository

    @Shared
    FileEntity fileEntity = buildFile(Utils.genFileMessage())


    @Transactional
    def "save file"() {
        when:
            repository.save(fileEntity)

        then:
            repository.findByFilenameAndOwner(fileEntity.getFilename(), fileEntity.getOwner())
    }

    FileEntity buildFile(FileMessage message) {
        FileEntity file = new FileEntity()
        file.setData(message.getData())
        file.setFilename(message.getFilename())
        file.setOwner(message.getOwner())

        return file
    }
}
