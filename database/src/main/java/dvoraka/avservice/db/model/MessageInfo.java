package dvoraka.avservice.db.model;

import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.AvMessageInfoData;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.common.data.DefaultAvMessageInfo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

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

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


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

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Date(created.getTime());
    }

    @Override
    public AvMessageInfo avMessageInfo() {
        return new DefaultAvMessageInfo.Builder(getUuid())
                .source(AvMessageSource.valueOf(getSource()))
                .serviceId(getServiceId())
                .created(getCreated().toInstant())
                .build();
    }
}
