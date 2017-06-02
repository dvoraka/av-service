package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.storage.ExistingFileException;
import dvoraka.avservice.storage.FileNotFoundException;
import dvoraka.avservice.storage.FileServiceException;
import org.springframework.transaction.annotation.Transactional;

/**
 * File service synchronous interface.
 */
public interface FileService {

    /**
     * Saves a file from a message.
     *
     * @param message the save request/message
     * @throws ExistingFileException if the file already exists
     * @throws FileServiceException  if other service problem occurs
     */
    void saveFile(FileMessage message) throws FileServiceException;

    /**
     * Loads a file.
     *
     * @param message the load request/message
     * @return the file response
     * @throws FileNotFoundException if the file is not found
     * @throws FileServiceException  if other service problem occurs
     */
    FileMessage loadFile(FileMessage message) throws FileServiceException;

    /**
     * Updates a file.
     *
     * @param message the update request/message
     * @throws FileNotFoundException if the file is not found
     * @throws FileServiceException  if other service problem occurs
     */
    void updateFile(FileMessage message) throws FileServiceException;

    /**
     * Deletes a file.
     *
     * @param message the delete request/message
     * @throws FileServiceException if other service problem occurs
     */
    void deleteFile(FileMessage message) throws FileServiceException;

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
    @Transactional(readOnly = true)
    default boolean exists(FileMessage message) {
        return exists(message.getFilename(), message.getOwner());
    }
}
