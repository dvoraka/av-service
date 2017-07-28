package dvoraka.avservice.db.repository.db;

import dvoraka.avservice.db.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DB file repository.
 */
@Repository
public interface DbFileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByFilenameAndOwner(String filename, String owner);

    Long removeByFilenameAndOwner(String filename, String owner);
}
