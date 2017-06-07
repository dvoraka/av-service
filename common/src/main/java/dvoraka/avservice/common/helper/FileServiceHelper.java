package dvoraka.avservice.common.helper;

import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;

/**
 * Helper for file service data structures.
 */
public interface FileServiceHelper extends UuidHelper {

    default FileMessage fileDataMessage(
            MessageType type, String filename, String owner, byte[] data
    ) {
        return new DefaultAvMessage.Builder(genUuidStr())
                .type(type)
                .filename(filename)
                .owner(owner)
                .data(data)
                .build();
    }

    default FileMessage fileMessage(MessageType type, String filename, String owner) {
        return fileDataMessage(type, filename, owner, null);
    }

    default FileMessage fileSaveMessage(String filename, String owner, byte[] data) {
        return fileDataMessage(MessageType.FILE_SAVE, filename, owner, data);
    }

    default FileMessage fileLoadMessage(String filename, String owner) {
        return fileMessage(MessageType.FILE_LOAD, filename, owner);
    }

    default FileMessage fileLoadMessage(FileMessage message) {
        return fileLoadMessage(message.getFilename(), message.getOwner());
    }

    default FileMessage fileUpdateMessage(String filename, String owner, byte[] data) {
        return fileDataMessage(MessageType.FILE_UPDATE, filename, owner, data);
    }

    default FileMessage fileUpdateMessage(FileMessage message, byte[] data) {
        return fileUpdateMessage(message.getFilename(), message.getOwner(), data);
    }

    default FileMessage fileDeleteMessage(String filename, String owner) {
        return fileMessage(MessageType.FILE_DELETE, filename, owner);
    }
}
