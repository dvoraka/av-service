package dvoraka.avservice.service;

import dvoraka.avservice.avprogram.AVProgram;
import dvoraka.avservice.exception.FileSizeException;
import dvoraka.avservice.exception.ScanErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Default AV service implementation.
 */
public class DefaultAVService implements AVService {

    private static final Logger log = LogManager.getLogger(DefaultAVService.class.getName());

    /**
     * Default max file size in bytes.
     */
    private static final long DEFAULT_MAX_FILE_SIZE = 10_000_000;

    private long maxFileSize;

    @Autowired
    private AVProgram avProgram;


    public DefaultAVService() {
        maxFileSize = DEFAULT_MAX_FILE_SIZE;
    }

    public DefaultAVService(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public boolean scanStream(byte[] bytes) throws ScanErrorException {
        return avProgram.scanStream(bytes);
    }

    @Override
    public String scanStreamWithInfo(byte[] bytes) {
        return avProgram.scanStreamWithInfo(bytes);
    }

    @Override
    public boolean scanFile(File file) throws ScanErrorException, FileSizeException {
        byte[] bytes;
        try {
            long size = Files.size(file.toPath());
            if (size > getMaxFileSize()) {
                log.warn("Too big file: " + size + " bytes.");
                throw new FileSizeException(
                        "File is too big: " + size + " bytes, max is: " + getMaxFileSize());
            }
            bytes = Files.readAllBytes(file.toPath());

            return scanStream(bytes);
        } catch (IOException e) {
            log.warn("File error!", e);
            throw new ScanErrorException("File error.", e);
        }
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
