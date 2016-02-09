package dvoraka.avservice;

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

    @Override
    public boolean scanFile(File file) {
        return false;
    }
}
