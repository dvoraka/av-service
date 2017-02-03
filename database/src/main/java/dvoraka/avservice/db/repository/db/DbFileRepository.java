package dvoraka.avservice.db.repository.db;

import dvoraka.avservice.db.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DB file repository.
 */
@Repository
public interface DbFileRepository extends JpaRepository<File, Long> {

    Optional<File> findByFilenameAndOwner(String filename, String owner);

    Long removeByFilenameAndOwner(String filename, String owner);
}
