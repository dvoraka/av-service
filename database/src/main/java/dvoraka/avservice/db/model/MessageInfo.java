package dvoraka.avservice.db.model;

import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.AvMessageInfoData;
import dvoraka.avservice.common.data.DefaultAvMessageInfo;
import dvoraka.avservice.common.data.InfoSource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

/**
 * Message info entity.
 */
@Entity
public class MessageInfo implements AvMessageInfoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String uuid;
    private String source;
    private String serviceId;

    private Instant created;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public AvMessageInfo avMessageInfo() {
        return new DefaultAvMessageInfo.Builder(getUuid())
                .source(InfoSource.valueOf(getSource()))
                .serviceId(getServiceId())
                .created(getCreated())
                .build();
    }
}
