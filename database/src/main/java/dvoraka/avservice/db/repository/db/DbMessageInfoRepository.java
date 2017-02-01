package dvoraka.avservice.db.repository.db;

import dvoraka.avservice.db.model.MessageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * DB message info repository.
 */
@Repository
public interface DbMessageInfoRepository extends JpaRepository<MessageInfo, Long> {

    MessageInfo findByUuid(String uuid);

    List<MessageInfo> findByCreatedBetween(Date from, Date to);
    //TODO
//    List<MessageInfo> findByCreatedBetween(Instant from, Instant to);
}