package dvoraka.avservice.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Statistics service.
 */
@Service
public class DefaultStatsService implements StatsService {

    private final Messages messages;


    @Autowired
    public DefaultStatsService(Messages messages) {
        this.messages = messages;
    }
}
