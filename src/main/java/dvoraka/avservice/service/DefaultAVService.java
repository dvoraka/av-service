package dvoraka.avservice.service;

import dvoraka.avservice.AVProgram;
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

    @Autowired
    private AVProgram avProgram;


    @Override
    public boolean scanStream(byte[] bytes) {
        return avProgram.scanStream(bytes);
    }

    @Override
    public boolean scanFile(File file) {
        // TODO: check file size
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            // TODO: throw exception
            log.warn("File error!", e);
        }

        boolean result = false;
        if (bytes != null) {
            result = scanStream(bytes);
        } else {
            // TODO: throw exception
        }

        return result;
    }
}
