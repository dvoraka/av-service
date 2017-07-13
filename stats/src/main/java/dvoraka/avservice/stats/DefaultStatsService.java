package dvoraka.avservice.stats;

import dvoraka.avservice.common.data.InfoSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

/**
 * Statistics service.
 */
@Service
public class DefaultStatsService implements StatsService {

    private final Messages messages;


    @Autowired
    public DefaultStatsService(Messages messages) {
        this.messages = requireNonNull(messages);
    }

    @Override
    public long todayCount() {
        LocalDate today = LocalDate.now();
        Instant start = today.atStartOfDay(ZoneId.systemDefault()).toInstant();

        return messages
                .when(start, Instant.now())
                .filter(info -> info.getSource().equals(InfoSource.PROCESSOR))
                .count();
    }
}
