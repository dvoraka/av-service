package dvoraka.avservice.checker;

import java.io.IOException;

/**
 * Interface for testing classes.
 * <p>
 * Created by dvoraka on 4/20/14.
 */
@FunctionalInterface
public interface Tester {

    void startTest() throws IOException;
}
