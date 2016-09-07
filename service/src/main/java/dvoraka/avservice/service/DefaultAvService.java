package dvoraka.avservice.service;

import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.common.exception.FileSizeException;
import dvoraka.avservice.common.exception.ScanErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Default AV service implementation.
 */
public class DefaultAvService implements AvService {

    private static final Logger log = LogManager.getLogger(DefaultAvService.class.getName());
    /**
     * Default max file size in bytes.
     */
    private static final long DEFAULT_MAX_FILE_SIZE = 10_000_000;

    private final AvProgram avProgram;
    private long maxFileSize;
    private long maxArraySize;


    @Autowired
    public DefaultAvService(AvProgram avProgram) {
        this.avProgram = avProgram;
        maxFileSize = DEFAULT_MAX_FILE_SIZE;
        maxArraySize = avProgram.getMaxArraySize();
    }

    @Override
    public boolean scanBytes(byte[] bytes) throws ScanErrorException {
        if (bytes.length == 0) {
            return false;
        }

        checkSize(bytes.length);

        return avProgram.scanBytes(bytes);
    }

    @Override
    public String scanBytesWithInfo(byte[] bytes) throws ScanErrorException {
        if (bytes.length == 0) {
            return "";
        }

        checkSize(bytes.length);

        return avProgram.scanBytesWithInfo(bytes);
    }

    private void checkSize(long arraySize) throws ScanErrorException {
        if (arraySize > getMaxArraySize()) {
            throw new ScanErrorException(
                    "Array is too big: " + arraySize + ", max is: " + getMaxArraySize());
        }
    }

    @Override
    public boolean scanFile(File file) throws ScanErrorException {
        byte[] bytes;
        try {
            long size = Files.size(file.toPath());
            if (size > getMaxFileSize()) {
                log.warn("Too big file: " + size + " bytes.");
                throw new ScanErrorException("Too big file.", new FileSizeException(
                        "File is too big: " + size + " bytes, max is: " + getMaxFileSize()));
            }
            bytes = Files.readAllBytes(file.toPath());

            return scanBytes(bytes);
        } catch (IOException e) {
            log.warn("File error!", e);
            throw new ScanErrorException("File error.", e);
        }
    }

    @Override
    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public long getMaxArraySize() {
        return maxArraySize;
    }

    public void setMaxArraySize(long maxArraySize) {
        this.maxArraySize = maxArraySize;
    }
}
