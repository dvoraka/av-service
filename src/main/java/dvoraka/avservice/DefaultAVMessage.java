package dvoraka.avservice;

/**
 * Default AV message implementation.
 */
public class DefaultAVMessage implements AVMessage {

    private byte[] data;
    private boolean virus;
    AVMessageType type;


    public DefaultAVMessage(byte[] data) {
        this(data, null);
    }

    public DefaultAVMessage(byte[] data, AVMessageType type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getCorrelationId() {
        return null;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public AVMessageType getType() {
        return null;
    }

    @Override
    public AVMessage createResponse(boolean virus) {
        return null;
    }
}
