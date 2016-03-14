package dvoraka.avservice.service;

import dvoraka.avservice.AVProgram;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Default AV service implementation.
 */
public class DefaultAVService implements AVService {

    @Autowired
    private AVProgram avProgram;

    @Override
    public boolean scanStream(byte[] bytes) {
        return avProgram.scanStream(bytes);
    }

    // TODO: implement
    @Override
    public boolean scanFile(File file) {
        return true;
    }
}
