package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.storage.ExistingFileException;
import dvoraka.avservice.storage.FileNotFoundException;

/**
 * File service synchronous interface.
 */
public interface FileService {

    /**
     * Saves a file from a message.
     *
     * @param message the save request/message
     * @throws ExistingFileException if file already exists
     */
    void saveFile(FileMessage message) throws ExistingFileException;

    /**
     * Loads a file.
     *
     * @param message the load request/message
     * @return the file response
     */
    FileMessage loadFile(FileMessage message);

    /**
     * Updates a file.
     *
     * @param message the update request/message
     * @throws FileNotFoundException if the file is not found
     */
    void updateFile(FileMessage message) throws FileNotFoundException;

    /**
     * Deletes a file.
     *
     * @param message the delete request/message
     */
    void deleteFile(FileMessage message);

    /**
     * Checks if a file exists.
     *
     * @param filename the filename
     * @param owner    the owner
     * @return <tt>true</tt> if exists
     */
    boolean exists(String filename, String owner);

    /**
     * Checks if a file exists.
     *
     * @param message the file request/message
     * @return <tt>true</tt> if exists
     * @see FileService#exists(String, String)
     */
    default boolean exists(FileMessage message) {
        return exists(message.getFilename(), message.getOwner());
    }
}
