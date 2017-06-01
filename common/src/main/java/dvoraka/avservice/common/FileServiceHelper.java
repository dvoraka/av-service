package dvoraka.avservice.common;

import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;

/**
 * Helper for file service data structures.
 */
public interface FileServiceHelper {

    default FileMessage fileMessage(MessageType type, String filename, String owner) {
        return new DefaultAvMessage.Builder(Utils.genUuidString())
                .type(type)
                .filename(filename)
                .owner(owner)
                .build();
    }

    default FileMessage fileLoadMessage(String filename, String owner) {
        return fileMessage(MessageType.FILE_LOAD, filename, owner);
    }
}
