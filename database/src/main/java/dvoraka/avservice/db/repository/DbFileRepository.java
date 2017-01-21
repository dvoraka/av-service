package dvoraka.avservice.db.repository;

import dvoraka.avservice.db.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * DB file repository.
 */
@Repository
public interface DbFileRepository extends JpaRepository<File, Long> {

    File findByFilenameAndOwner(String filename, UUID uuid);
}
