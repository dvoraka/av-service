package dvoraka.avservice.db.model;

import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

/**
 * File entity.
 */
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"filename", "owner"})
        }
)
@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private byte[] data;
    private String filename;
    private String owner;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data.clone();
    }

    public void setData(byte[] data) {
        this.data = data.clone();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public FileMessage fileMessage(String originalId) {
        return new DefaultAvMessage.Builder(UUID.randomUUID().toString())
                .correlationId(originalId)
                .data(getData())
                .type(MessageType.FILE_RESPONSE)
                .filename(getFilename())
                .owner(getOwner())
                .build();
    }
}
