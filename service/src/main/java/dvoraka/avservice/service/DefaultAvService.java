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

    @Autowired
    private AvProgram avProgram;

    private static final Logger log = LogManager.getLogger(DefaultAvService.class.getName());

    /**
     * Default max file size in bytes.
     */
    private static final long DEFAULT_MAX_FILE_SIZE = 10_000_000;

    private long maxFileSize;


    public DefaultAvService() {
        maxFileSize = DEFAULT_MAX_FILE_SIZE;
    }

    public DefaultAvService(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public boolean scanStream(byte[] bytes) throws ScanErrorException {
        if (bytes.length == 0) {
            return false;
        } else {
            return avProgram.scanBytes(bytes);
        }
    }

    @Override
    public String scanStreamWithInfo(byte[] bytes) throws ScanErrorException {
        if (bytes.length == 0) {
            return "";
        } else {
            return avProgram.scanBytesWithInfo(bytes);
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

            return scanStream(bytes);
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

    public void setAvProgram(AvProgram program) {
        this.avProgram = program;
    }
}
