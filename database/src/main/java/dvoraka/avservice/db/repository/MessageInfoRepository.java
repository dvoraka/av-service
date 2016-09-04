package dvoraka.avservice.db.repository;

import dvoraka.avservice.db.model.MessageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Message info repository.
 */
@Repository
public interface MessageInfoRepository extends JpaRepository<MessageInfo, Long> {
}
