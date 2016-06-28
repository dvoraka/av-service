package dvoraka.avservice.checker;

import java.io.IOException;

/**
 * Interface for testing classes.
 */
@FunctionalInterface
public interface Tester {

    void startTest() throws IOException;
}
